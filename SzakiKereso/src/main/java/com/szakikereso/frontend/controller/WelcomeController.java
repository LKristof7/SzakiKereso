package com.szakikereso.frontend.controller;

import javafx.fxml.FXML;
import org.springframework.stereotype.Controller;

@Controller
public class WelcomeController {
    private Runnable browseAction;

    public void setBrowseAction(Runnable browseAction) {
        this.browseAction = browseAction;
    }

    @FXML
    public void onBrowse() {
        if(browseAction != null) {
            browseAction.run();
        }
    }
}
