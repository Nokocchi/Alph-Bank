package com.alphbank.reactivelogging.impl;

import org.springframework.stereotype.Component;

@Component
public class StarterImpl {

    private final boolean enabled;

    public StarterImpl(boolean enabled) {
        this.enabled = enabled;
    }

    public void test(){
        if(enabled){
            System.out.println("STARTER WORKS and is enabled!!");
        } else {
            System.out.println("STARTER WORKS and is disabled!!");
        }

    }

}