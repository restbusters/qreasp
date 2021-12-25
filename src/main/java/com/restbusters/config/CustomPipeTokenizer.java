package com.restbusters.config;

import org.aeonbits.owner.Tokenizer;

/**
 * @author Sasha Matsaylo on 10/10/19
 * @project qreasp
 */

public class CustomPipeTokenizer implements Tokenizer {

    // this logic can be as much complex as you need
    @Override
    public String[] tokens(String values) {
        return values.split("\\;", -1);
    }
}