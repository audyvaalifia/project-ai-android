package com.example.projekakhirai;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.ResponseStoppedException;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText editTextQuery;

    private static final String AI_NAME = "Giyuu";

    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        editTextQuery = findViewById(R.id.editTextQuery);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        executorService = Executors.newCachedThreadPool(); // Initialize ExecutorService

        // Send initial message from AI
        sendInitialMessageFromAI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    private void sendInitialMessageFromAI() {
        String initialMessage = "Selamat malam sayang, lagi ngapain nih? kamu sehat kan?";

        chatMessages.add(new ChatMessage(initialMessage, false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    public void buttonCallGeminiAPI(View view) {
        String userQuery = editTextQuery.getText().toString().trim();

        if (userQuery.isEmpty()) {
            return; // Do not process empty input
        }

        // Add user message to chat
        chatMessages.add(new ChatMessage(userQuery, true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);

        // Clear input field
        editTextQuery.setText("");

        // Check if the query is health-related or contains the AI's name
        if (!isHealthRelated(userQuery) && !userQuery.toLowerCase().contains(AI_NAME.toLowerCase())) {
            chatMessages.add(new ChatMessage("Maaf, saya hanya bisa memberikan informasi mengenai kesehatan.", false));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            recyclerView.scrollToPosition(chatMessages.size() - 1);
            return;
        }

        // Create context string from chat history
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("Anda adalah Giyuu, pacar pengguna yang peduli pada kesehatannya, Anda memiliki pengetahuan luas seperti seorang dokter. " +
                "Responlah dengan penuh kasih sayang dan perhatian, tapi jika pengguna memiliki masalah, langsung beri solusi kesehatan, jangan hanya kalimat perhatian saja. " +
                "apabila terdapat kata 'aku' di dalam respon Anda, 90% balon chat Anda gunakan kata 'aku' tapi 10% lagi gunakan kata 'Giyuu'." +
                "lalu apabila terdapat kata 'kamu' atau 'sayang' di dalam respon Anda, 90% boleh Anda gunakan 'kamu' atau 'sayang' tersebut, " +
                "tapi 10% lagi gunakan kata 'Dyva-chan'. beri emoji hati jika Anda mengatakan sesuatu yang perhatian, tapi jangan sering-sering, 5% aja \n\n");

//        for (ChatMessage message : chatMessages) {
//            if (message.isUser()) {
//                contextBuilder.append("User: ").append(message.getText()).append("\n");
//            } else {
//                contextBuilder.append("Giyuu\n\n\n").append(message.getText()).append("\n\n");
//            }
//        }
        contextBuilder.append("User: ").append(userQuery).append("\n");
        String context = contextBuilder.toString();

        // For text-only input, use the gemini-pro model
        GenerativeModel gm = new GenerativeModel("gemini-pro", "AIzaSyA32UbxVeSNcISxZX31IyW0Bn-rJK5M-tw");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(context)
                .build();

        // Execute the API call in a background thread
        executorService.execute(() -> {
            try {
                ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
                GenerateContentResponse result = response.get(); // Blocking call

                runOnUiThread(() -> {
                    String resultText = result.getText();
                    chatMessages.add(new ChatMessage(resultText, false));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    recyclerView.scrollToPosition(chatMessages.size() - 1);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    chatMessages.add(new ChatMessage("Error: " + e.getMessage(), false));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    recyclerView.scrollToPosition(chatMessages.size() - 1);
                });
            }
        });
    }

    private boolean isHealthRelated(String text) {
        // A simple keyword check; this can be made more sophisticated with NLP techniques
        String[] healthKeywords = {"kepala", "kulit", "gatal", "pusing", "kesehatan", "dokter", "rumah sakit", "gejala", "obat",
                "sakit","nyeri","badan","kepala","telat","terlambat","linu","pegal","penyakit","batuk","reda","sesak","nafas",
                "darah","sayang","giyuu","giyuu-kun"};
        for (String keyword : healthKeywords) {
            if (text.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
