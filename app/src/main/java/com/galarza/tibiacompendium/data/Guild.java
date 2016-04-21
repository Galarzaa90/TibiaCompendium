package com.galarza.tibiacompendium.data;

import java.util.ArrayList;
import java.util.List;

public class Guild {
    private String name;
    private final List<GuildMember> memberList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GuildMember> getMemberList() {
        return memberList;
    }

    public int getOnlineCount(){
        int count = 0;
        for (GuildMember member: memberList){
            if (member.isOnline()){
                count++;
            }
        }
        return count;
    }

    public boolean addMember(GuildMember member){
        return memberList.add(member);
    }
}