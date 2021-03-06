package com.simon816.chatui.pagination;

import com.simon816.chatui.ChatUI;
import com.simon816.chatui.PlayerChatView;
import com.simon816.chatui.impl.ImplementationPagination;
import com.simon816.chatui.util.Utils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.Optional;

public class TabbedPaginationList implements PaginationList {

    private final PaginationList list;
    private final PaginationService service;

    public TabbedPaginationList(PaginationList list, PaginationService service) {
        this.list = list;
        this.service = service;
    }

    @Override
    public Iterable<Text> getContents() {
        return this.list.getContents();
    }

    @Override
    public Optional<Text> getTitle() {
        return this.list.getTitle();
    }

    @Override
    public Optional<Text> getHeader() {
        return this.list.getHeader();
    }

    @Override
    public Optional<Text> getFooter() {
        return this.list.getFooter();
    }

    @Override
    public Text getPadding() {
        return this.list.getPadding();
    }

    @Override
    public int getLinesPerPage() {
        return this.list.getLinesPerPage();
    }

    @Override
    public void sendTo(MessageReceiver receiver) {
        if (!(receiver instanceof CommandSource)) {
            this.list.sendTo(receiver);
            return;
        }
        CommandSource sendTo = Utils.getRealSource((CommandSource) receiver);
        if (!(sendTo instanceof Player)) {
            this.list.sendTo(receiver);
            return;
        }
        PlayerChatView view = ChatUI.getView(sendTo);
        CommandSource newReceiver = new PaginationSourceWrapper(view, (CommandSource) receiver, getTitle().orElse(null));
        this.list.sendTo(newReceiver);
        ImplementationPagination.modify(this.service, newReceiver, sendTo);
    }

}
