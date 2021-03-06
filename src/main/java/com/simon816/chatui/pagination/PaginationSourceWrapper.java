package com.simon816.chatui.pagination;

import com.simon816.chatui.PlayerChatView;
import com.simon816.chatui.tabs.PaginationTab;
import com.simon816.chatui.util.ForwardingSource;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

public class PaginationSourceWrapper extends ForwardingSource {

    public final PlayerChatView view;
    private final PaginationTab tab;

    public PaginationSourceWrapper(PlayerChatView view, CommandSource actualSource, Text title) {
        super(actualSource);
        this.view = view;
        this.tab = new PaginationTab(this, title == null ? Text.of("Pagination") : title);
        view.getWindow().addTab(this.tab, true);
        view.update();
    }

    @Override
    public void sendMessage(Text message) {
        this.tab.appendMessage(message);
    }

    @Override
    public void sendMessages(Iterable<Text> messages) {
        this.tab.setPage(messages);
    }

    @Override
    public void sendMessages(Text... messages) {
        this.tab.setPage(messages);
    }

}
