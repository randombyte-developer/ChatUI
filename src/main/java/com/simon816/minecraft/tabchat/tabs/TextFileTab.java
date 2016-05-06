package com.simon816.minecraft.tabchat.tabs;

import com.google.common.collect.Lists;
import com.simon816.minecraft.tabchat.PlayerChatView;
import com.simon816.minecraft.tabchat.PlayerContext;
import com.simon816.minecraft.tabchat.TabbedChat;
import com.simon816.minecraft.tabchat.util.TextUtils;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class TextFileTab extends BufferedTab {

    private final List<Text> buffer = Lists.newArrayList();
    private int caretLine;
    private int caretCol;

    public TextFileTab(PlayerChatView view) {
        this.caretLine = 1;
        enableInputCapturing(view);
    }

    @Override
    public LiteralText getTitle() {
        return Text.of("New File");
    }

    @Override
    public Text draw(PlayerContext ctx) {
        Text.Builder builder = Text.builder();
        int remainingHeight = ctx.height;
        bufferLoop: for (int i = 0; i < this.buffer.size(); i++) {
            String side = ":: ";
            Text message = this.buffer.get(i);
            List<Text> lines = TextUtils.splitLines(message, ctx.width - TextUtils.getStringWidth(side, false), ctx.player.isChatColorsEnabled());
            for (Text line : lines) {
                if (--remainingHeight < 0) {
                    break bufferLoop;
                }
                int ln = ctx.height - remainingHeight;
                String str = line.toPlain();
                Text.Builder b = Text.builder();
                char[] chars = str.toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    int col = j;
                    b.append(Text.builder(chars[j]).onClick(TextActions.executeCallback(src -> {
                        this.caretLine = ln;
                        this.caretCol = col;
                        TabbedChat.getView(ctx.player).update();
                    })).color(ln == this.caretLine && j == this.caretCol ? TextColors.RED : TextColors.WHITE).toText());
                }
                line = b.build();
                if (ln == this.caretLine) {
                    side = "@";
                }
                builder.append(Text.of(TextColors.GRAY, TextActions.executeCallback(src -> {
                    this.caretLine = ln;
                    this.caretCol = 0;
                    TabbedChat.getView(ctx.player).update();
                }), side), line, Text.NEW_LINE);
            }
        }
        if (remainingHeight > 0) {
            while (remainingHeight-- > 0) {
                String side = ":: \n";
                int ln = ctx.height - remainingHeight;
                if (ln == this.caretLine) {
                    side = "@\n";
                }
                builder.append(Text.of(TextColors.GRAY, TextActions.executeCallback(src -> {
                    this.caretLine = ln;
                    this.caretCol = 0;
                    TabbedChat.getView(ctx.player).update();
                }), side));
            }
        }
        return builder.build();
    }

    @Override
    public void appendMessage(Text message) {
        while (this.buffer.size() < this.caretLine) {
            this.buffer.add(Text.EMPTY);
        }
        if (this.buffer.size() >= this.caretLine) {
            Text existing = this.buffer.get(this.caretLine - 1);
            String str = existing.toPlain();
            String newStr;
            if (str.length() > this.caretCol) {
                newStr = str.substring(0, this.caretCol + 1) + message.toPlain() + str.substring(this.caretCol + 1);
                this.caretCol++;
            } else {
                newStr = str + message.toPlain();
            }
            this.buffer.set(this.caretLine - 1, Text.of(newStr));
        } else {
            this.buffer.add(message);
        }
        this.caretCol += message.toPlain().length() - 1;
    }

}
