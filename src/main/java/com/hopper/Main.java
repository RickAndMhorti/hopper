package com.hopper;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Timer;
import java.util.TimerTask;

@Route("")
public class Main extends VerticalLayout{

    private ContentDatabase contentDatabase;
    private TextField userInput = new TextField("Input your content here");

    private TextField KeyOffset = new TextField("Enter your Key Offset here");

    private TextField KeyInput = new TextField("Enter your given key here");

    private Paragraph HopperKey = new Paragraph("Welcome to Hopper!");

    //outputGrid is where the user's uploaded text is returned
    private Grid<Content> outputGrid = new Grid<>(Content.class);
    private Binder<Content> binder = new Binder<>(Content.class);

    public Main(ContentDatabase contentDatabase){
        this.contentDatabase = contentDatabase;

        binder.forField(userInput).bind(Content::getUserInput,Content::setUserInput);
        binder.forField(KeyOffset).bind(Content::getKeyOffset,Content::setKeyOffset);

        var retrieveButton = new Button("Retrieve");
        retrieveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        retrieveButton.addClickListener(e -> {
            retrieveOutput();
        });

        var wipeButton = new Button("Wipe");
        retrieveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        wipeButton.addClickListener(e -> {
            wipeContentDatabase();
        });

        outputGrid.setColumns("id","userInput");
        add(submitUserContent(), outputGrid, retrieveButton, wipeButton);


    }

    /**
     *
     * @return
     */
    public Component submitUserContent(){

        var layout = new VerticalLayout();
        var uploadButton = new Button("Upload");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        layout.add(userInput, KeyOffset, uploadButton, KeyInput, HopperKey);

        binder.bindInstanceFields(this);

        uploadButton.addClickListener(click -> {
            try{
                var content = new Content();
                binder.writeBean(content);
                binder.readBean(new Content());
                contentDatabase.save(content);

                userInput.clear();
                KeyOffset.clear();
                HopperKey.setText("Your HopperKey is: " + content.GetHopperkey());

            } catch (ValidationException e) {
                //
            }


        });

        return layout;
    }
    private void retrieveOutput(){

        outputGrid.setItems(contentDatabase.findAll());
    }

    private void wipeContentDatabase(){
        TimerTask timedWipe = new TimerTask() {
            @Override
            public void run() {
                contentDatabase.deleteAll();
            }
        };

        Timer timer = new Timer("Timer");

        long delay = 10000;
        //delay = time in milliseconds, default is 10000 ms/ 10s

        timer.schedule(timedWipe,delay);
    }
}
