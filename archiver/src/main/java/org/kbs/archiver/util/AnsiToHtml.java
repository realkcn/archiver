package org.kbs.archiver.util;

public class AnsiToHtml {
	private static final int STATE_ESC_SET = 0x01;
	private static final int STATE_FONT_SET = 0x02;
	private static final int STATE_NEW_LINE = 0x04;
	private static final int STATE_QUOTE_LINE = 0x08;
	private static final int STATE_NONE = 0x00;
	private static final int STATE_UBB_START = 0x10;
	private static final int STATE_UBB_MIDDLE = 0x20;
	private static final int STATE_UBB_END = 0x40;
	private static final int STATE_TEX_SET = 0x80;
	private static final int FONT_STYLE_UL = 0x0100;
	private static final int FONT_STYLE_BLINK = 0x0200;
	private static final int FONT_STYLE_ITALIC = 0x0400;

	private static final int FONT_FG_BOLD = 0x08;
	private static final int FONT_COLOR_BLACK = 0x00;
	private static final int FONT_COLOR_RED = 0x01;
	private static final int FONT_COLOR_GREEN = 0x02;
	private static final int FONT_COLOR_YELLOW = 0x03;
	private static final int FONT_COLOR_BULE = 0x04;
	private static final int FONT_COLOR_MAGENTA = 0x05;
	private static final int FONT_COLOR_CYAN = 0x06;
	private static final int FONT_COLOR_WHITE = 0x07;

	private static final int FONT_STYLE_QUOTE = 0x0000;
	private static final int FONT_COLOR_QUOTE = FONT_COLOR_CYAN;

	private static final int FONT_BG_SET = 0x80;

	private static int style_set_fg(int s, int c) {
		s = (s & ~0x07) | (c & 0x07);
		return s;
	}

	private static int style_set_bg(int s, int c) {
		s = (s & ~0x70) | ((c & 0x07) << 4);
		return s;
	}

	private static int style_get_fg(int s) {
		return s & 0x0F;
	}

	private static int style_get_bg(int s) {
		return (s & 0x70) >> 4;
	}

	private static int style_clr_fg(int s) {
		return (s &= ~0x0F);
	}

	private static int style_clr_bg(int s) {
		return (s &= ~0xF0);
	}

	private static boolean isset(int s, int b) {
		return (s & b) != 0;
	}

	private static void print_font_style(int style, StringBuilder output) {
		String font_str;
		StringBuilder font_style = new StringBuilder();
		int bg;

		if (isset(style, FONT_BG_SET)) {
			bg = 8;
		} else
			bg = style_get_bg(style);
		String font_class = String.format("f%01d%02d", bg, style_get_fg(style));
		if (isset(style, FONT_STYLE_UL))
			font_style.append("text-decoration: underline; ");
		if (isset(style, FONT_STYLE_ITALIC))
			font_style.append("font-style: italic; ");
		if (font_style.length() != 0)
			font_str = String.format("<font class=\"%s\" style=\"%s\">",
					font_class, font_style);
		else
			font_str = String.format("<font class=\"%s\">", font_class);
//		System.out.println(String.format("print font:%x %s",style,font_str));
		output.append(font_str);
	}

	private static void print_raw_ansi(char ch, StringBuilder output) {
		switch (ch) {
		case 0x1b:
			output.append('*');
			break;
		case '\n':
			output.append("<br />");
			break;
		case '&':
			output.append("&amp;");
			break;
		case '<':
			output.append("&lt;");
			break;
		case '>':
			output.append("&gt;");
			break;
		case ' ':
			output.append("&nbsp;");
			break;
		default:
			output.append(ch);
		}
	}

	private static void print_raw_ansi(String buf, StringBuilder output) {
		for (int i = 0; i < buf.length(); i++) {
			char ch = buf.charAt(i);
			print_raw_ansi(ch, output);
		}
	}

	public static int generate_font_style(int style, int[] ansi_val, int len) {
		int i;
		int color;

		for (i = 0; i < len; i++) {
			if (ansi_val[i] == 0)
				style = 0;
			else if (ansi_val[i] == 1)
				style |= FONT_FG_BOLD;
			else if (ansi_val[i] == 4)
				style |= FONT_STYLE_UL;
			else if (ansi_val[i] == 5)
				style |= FONT_STYLE_BLINK;
			else if (ansi_val[i] >= 30 && ansi_val[i] <= 37) {
				color = ansi_val[i] - 30;
				style = style_set_fg(style, color);
			} else if (ansi_val[i] >= 40 && ansi_val[i] <= 47) {
				/*
				 * user explicitly specify background color
				 */
				/*
				 * STYLE_SET(*style, FONT_BG_SET);
				 */
				color = ansi_val[i] - 40;
				style = style_set_bg(style, color);
			}
		}
		return style;
	}

	public static String ansiToHtml(String src) {
		StringBuilder output = new StringBuilder();
		int ansi_state = 0;
		int[] ansi_val = new int[80];
		ansi_val[0] = 0;
		int ival = 0;
		int font_style = 0;
		int ansi_begin = 0;
		int ansi_end;
		int srclength = src.length();
		for (int ptr=0; ptr < srclength; ptr++) {
			char ch;
			ch = src.charAt(ptr);
			if (isset(ansi_state, STATE_NEW_LINE)) {
				ansi_state &= ~STATE_NEW_LINE;
				if ((ptr < srclength - 1)
						&& (!isset(ansi_state, STATE_TEX_SET)) && (ch == ':')
						&& (src.charAt(ptr + 1) == ' ')) {
					// 引用
					ansi_state |= STATE_QUOTE_LINE;
					if ((ansi_state & STATE_FONT_SET) != 0) {
						// 封闭<font>
						output.append("</font>");
					}
					// 设置引用的字体
					font_style |= FONT_STYLE_QUOTE;
					font_style = style_set_fg(font_style, FONT_COLOR_QUOTE);
					font_style = style_clr_bg(font_style);
					print_font_style(font_style, output);
					output.append(':');
					ansi_state |= STATE_FONT_SET;
					ansi_state &= ~STATE_ESC_SET;
					// 清除ansi缓存
					ansi_val[0] = 0;
					ival = 0;
					continue;
				} else {
					ansi_state &= ~STATE_QUOTE_LINE;
				}
			}
			/*
			 * is_tex 情况下，\[upload 优先匹配 \[ 而不是 [upload is_tex 情况下应该还有一个问题是 *[\[
			 * 等，不过暂时不管了 - atppp
			 */
			if (ptr < (srclength - 1) && !isset(ansi_state, STATE_TEX_SET)
					&& (ch == 0x1b) && (src.charAt(ptr + 1) == '[')) {
				if (isset(ansi_state, STATE_ESC_SET)) {
					/*
					 * [*[ or *[13;24*[
					 */
					ansi_end = ptr - 1;
					print_raw_ansi(src.substring(ansi_begin, ansi_end), output);
				}
				ansi_state |= STATE_ESC_SET;
				ansi_begin = ptr;
				ptr++; /* skip the next '[' character */
			} else if (ch == '\n') {
				if (isset(ansi_state, STATE_ESC_SET)) {
					/*
					 * [\n or *[13;24\n
					 */
					ansi_end = ptr - 1;
					print_raw_ansi(src.substring(ansi_begin, ansi_end), output);
					ansi_state &= ~STATE_ESC_SET;
				}
				if (isset(ansi_state, STATE_QUOTE_LINE)) {
					/*
					 * end of a quoted line
					 */
					output.append(" </font>");
					font_style &= ~FONT_STYLE_QUOTE;
					ansi_state &= ~STATE_FONT_SET;
				}
				if (!isset(ansi_state, STATE_TEX_SET)) {
					output.append(" <br /> ");
				}
				ansi_state &= ~STATE_QUOTE_LINE;
				ansi_state |= STATE_NEW_LINE;
			} else {
				if (isset(ansi_state, STATE_ESC_SET)) {
					// 处理ansi转义符
					if (ch == 'm') {
						// *[0;1;4;31m
						if (isset(ansi_state, STATE_FONT_SET)) {
							output.append("</font>");
							ansi_state &= ~STATE_FONT_SET;
						}
						if (ptr < srclength - 1) {
							font_style = generate_font_style(font_style,
									ansi_val, ival + 1);
							if (isset(ansi_state, STATE_QUOTE_LINE))
								font_style |= FONT_STYLE_QUOTE;
							print_font_style(font_style, output);
							ansi_state |= STATE_FONT_SET;
							ansi_state &= ~STATE_ESC_SET;
							/*
							 * STYLE_ZERO(font_style);
							 */
							/*
							 * clear ansi_val[] array
							 */
							ansi_val[0] = 0;
							ival = 0;
						}
					} else if (Character.isLetter(ch)) {
						/*
						 * [23;32H ignore it
						 */
						ansi_state &= ~STATE_ESC_SET;
						font_style = 0;
						/*
						 * clear ansi_val[] array
						 */
						ansi_val[0] = 0;
						ival = 0;
						continue;
					} else if (ch == ';') {
						if (ival < ansi_val.length - 1) {
							ival++; /* go to next ansi_val[] element */
							ansi_val[ival] = 0;
						}
					} else if (ch >= '0' && ch <= '9') {
						ansi_val[ival] *= 10;
						ansi_val[ival] += (ch - '0');
					} else {
						/*
						 * [1;32/XXXX or *[* or *[[
						 */
						/*
						 * not a valid ANSI string, just output it
						 */
						ansi_end = ptr;
						print_raw_ansi(src.substring(ansi_begin, ansi_end),
								output);
						ansi_state &= ~STATE_ESC_SET;
						/*
						 * clear ansi_val[] array
						 */
						ival = 0;
						ansi_val[ival] = 0;
					}
				} else
					// !isset ESC_SET
					print_raw_ansi(ch, output);
			}
		}
		if (isset(ansi_state, STATE_FONT_SET)) {
			output.append("</font>");
			ansi_state &= ~STATE_FONT_SET;
		}
		return new String(output);
	}
}
