package com.Assignment.VotingClient.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VotingInfo implements Serializable {
    private String voterName;
    private String boothName;
    private String party;
}
