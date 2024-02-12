package com.exm.flipkartclone.util;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageStructure {
private String to;
private String subject;
private Date sendDate;
private String text;


}
