package com.restbusters.core.resource;

import org.aeonbits.owner.Tokenizer;

/**
 * @author restbusters on 10/15/18
 * @project qreasp
 */

public class CustomPipeTokenizer implements Tokenizer {

    // this logic can be as much complex as you need
    @Override
    public String[] tokens(String values) {
        return values.split("\\|", -1);
    }
}