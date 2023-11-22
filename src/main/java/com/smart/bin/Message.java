package com.smart.bin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor @ToString @Getter @Setter @AllArgsConstructor
public class Message {
	private String content;
	private String type;
}
