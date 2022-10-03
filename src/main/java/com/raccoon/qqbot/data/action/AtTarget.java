package com.raccoon.qqbot.data.action;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AtTarget {
    private long targetId;
    private UserAction.Permission targetPermission;
}
