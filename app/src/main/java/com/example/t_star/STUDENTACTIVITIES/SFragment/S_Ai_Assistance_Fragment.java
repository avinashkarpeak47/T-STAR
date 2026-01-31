
package com.example.t_star.STUDENTACTIVITIES.SFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.AIService;
import com.example.t_star.STUDENTACTIVITIES.Adaptar.ChatAdapter;
import com.example.t_star.STUDENTACTIVITIES.ChatMessage;
import com.example.t_star.STUDENTACTIVITIES.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class S_Ai_Assistance_Fragment extends Fragment {

    RecyclerView recyclerView;
    EditText etMessage;
    ImageButton btnSend;

    List<ChatMessage> chatList = new ArrayList<>();
    ChatAdapter adapter;
    AIService api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_s__ai_assistant_, container, false);

        recyclerView = view.findViewById(R.id.chatRecycler);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);

        adapter = new ChatAdapter(chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        api = RetrofitClient.getClient().create(AIService.class);

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {

        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty()) return;

        chatList.add(new ChatMessage(msg, true));
        adapter.notifyItemInserted(chatList.size() - 1);
        recyclerView.scrollToPosition(chatList.size() - 1);
        etMessage.setText("");

        JsonObject body = new JsonObject();


        // REQUIRED
        body.addProperty("model", "openai/gpt-3.5-turbo");

        // REQUIRED
        JsonArray messages = new JsonArray();

        // USER MESSAGE
        JsonObject user = new JsonObject();
        user.addProperty("role", "user");
        user.addProperty("content", msg);

        // ADD MESSAGE
        messages.add(user);

        // ATTACH
        body.add("messages", messages);



       /* JsonArray messages = new JsonArray();

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", msg);

        messages.add(userMsg);

        body.add("messages", messages);*/

        String auth = "Bearer " + getString(R.string.hf_api_key);

        api.askAI(auth, body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    addBot("AI error: " + response.code());
                    return;
                }

                JsonObject message = response.body()
                        .getAsJsonArray("choices")
                        .get(0)
                        .getAsJsonObject()
                        .getAsJsonObject("message");

                String reply = message.get("content").getAsString().trim();

                if (reply.isEmpty()) {
                    addBot("AI returned empty response.");
                    return;
                }

                addBot(reply);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                addBot("Network error.");
            }
        });


    }

    private void addBot(String text) {
        chatList.add(new ChatMessage(text, false));
        adapter.notifyItemInserted(chatList.size() - 1);
        recyclerView.scrollToPosition(chatList.size() - 1);
    }
}
