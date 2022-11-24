package com.example.ezhoop.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.example.ezhoop.R;
import com.google.gson.JsonElement;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.UserId;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.UUID;

public class StartTrainingActivity extends AppCompatActivity {
    private static final String CHANNEL_GAME = "game";
    private static final String CHANNEL_SCORE = "score";

    private PNConfiguration pnConfiguration;
    private PubNub pubnub;
    private boolean isGameRunning = false;
    private int score;

    private TextView textScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_training);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setupPubnub();

        textScore = findViewById(R.id.score);

        Button btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(v -> {
            pubnub.publish().channel(CHANNEL_GAME).message(!isGameRunning ? "start" : "end").async((result, status1) -> {
                if (!status1.isError()) {
                    if (!isGameRunning) {
                        isGameRunning = true;
                        score = 0;
                        btnStart.setText(getString(R.string.btn_end));
                    } else {
                        isGameRunning = false;
                        btnStart.setText(getString(R.string.btn_start));
                        textScore.setText("");

                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Game finished!");
                            builder.setMessage("Scored " + score + " in total.");
                            builder.setPositiveButton("Okay", null);
                            builder.show();
                        });
                    }
                } else {

                }
            });
        });
    }

    private void setupPubnub() {
        try {
            pnConfiguration = new PNConfiguration(new UserId(UUID.randomUUID().toString()));
            pnConfiguration.setSubscribeKey("sub-c-143f84aa-6dcf-405a-8805-96edb0b9554f");
            pnConfiguration.setPublishKey("pub-c-c1b540b3-d0af-4fea-8316-245dc2589f12");

            pubnub = new PubNub(pnConfiguration);

            pubnub.addListener(new SubscribeCallback() {
                @Override
                public void status(@NonNull PubNub pubnub, @NonNull PNStatus status) {
                    if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                        // This event happens when radio / connectivity is lost
                    } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                        // Connect event. You can do stuff like publish, and know you'll get it.
                        // Or just use the connected event to confirm you are subscribed for
                        // UI / internal notifications, etc

                        if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
//                            pubnub.publish().channel(CHANNEL_SCORE).message("start").async((result, status1) -> {
//                                // Check whether request successfully completed or not.
//                                if (!status1.isError()) {
//                                    // Message successfully published to specified channel.
//                                }
//                                // Request processing failed.
//                                else {
//                                    // Handle message publish error. Check 'category' property to find out possible issue
//                                    // because of which request did fail.
//                                    //
//                                    // Request can be resent using: [status retry];
//                                }
//                            });
                        }
                    } else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
                        // Happens as part of our regular operation. This event happens when
                        // radio / connectivity is lost, then regained.
                    } else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
                        // Handle messsage decryption error. Probably client configured to
                        // encrypt messages and on live data feed it received plain text.
                    }
                }

                @Override
                public void message(@NonNull PubNub pubnub, @NonNull PNMessageResult message) {
                    // Handle new message stored in message.message
                    if (message.getChannel() != null) {
                        // Message has been received on channel group stored in
                        // message.getChannel()
                    } else {
                        // Message has been received on channel stored in
                        // message.getSubscription()
                    }

                    JsonElement receivedMessageObject = message.getMessage();
                    score = Integer.parseInt(receivedMessageObject.toString());
                    runOnUiThread(() -> {
                        textScore.setText("Scored: " + score);
                    });
                    // extract desired parts of the payload, using Gson
//                    String msg = message.getMessage().getAsJsonObject().get("msg").getAsString();
//                    System.out.println("msg content: " + msg);

            /*
                log the following items with your favorite logger
                    - message.getMessage()
                    - message.getSubscription()
                    - message.getTimetoken()
            */
                }

                @Override
                public void presence(@NonNull PubNub pubnub, @NonNull PNPresenceEventResult presence) {

                }

                @Override
                public void signal(@NotNull PubNub pubnub, @NotNull PNSignalResult pnSignalResult) {

                }

                @Override
                public void uuid(@NotNull PubNub pubnub, @NotNull PNUUIDMetadataResult pnUUIDMetadataResult) {

                }

                @Override
                public void channel(@NotNull PubNub pubnub, @NotNull PNChannelMetadataResult pnChannelMetadataResult) {

                }

                @Override
                public void membership(@NotNull PubNub pubnub, @NotNull PNMembershipResult pnMembershipResult) {

                }

                @Override
                public void messageAction(@NotNull PubNub pubnub, @NotNull PNMessageActionResult pnMessageActionResult) {

                }

                @Override
                public void file(@NotNull PubNub pubnub, @NotNull PNFileEventResult pnFileEventResult) {

                }
            });

            pubnub.subscribe().channels(Collections.singletonList(CHANNEL_SCORE)).execute();
        } catch (PubNubException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}