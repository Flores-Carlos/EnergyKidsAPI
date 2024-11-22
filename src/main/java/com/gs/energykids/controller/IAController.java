package com.gs.energykids.controller;

import com.gs.energykids.exception.ResourceNotFoundException;
import com.gs.energykids.model.Consumo;
import com.gs.energykids.model.DicaEconomia;
import com.gs.energykids.model.Dispositivo;
import com.gs.energykids.repository.ConsumoRepository;
import com.gs.energykids.repository.DicaEconomiaRepository;
import com.gs.energykids.repository.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/ia")
public class IAController {

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @Autowired
    private ConsumoRepository consumoRepository;

    @Autowired
    private DicaEconomiaRepository dicaEconomiaRepository;

//    @PostMapping("/gerar-dicas/{usuarioId}")
//    public ResponseEntity<List<DicaEconomia>> gerarDicas(@PathVariable Long usuarioId) {
//        List<Dispositivo> dispositivos = dispositivoRepository.findByUsuarioId(usuarioId);
//
//        if (dispositivos.isEmpty()) {
//            throw new ResourceNotFoundException("Nenhum dispositivo encontrado para o usuário ID " + usuarioId);
//        }
//
//        StringBuilder promptBuilder = new StringBuilder("Baseado nos seguintes dispositivos e consumos, sugira dicas de economia de energia:\n");
//
//        for (Dispositivo dispositivo : dispositivos) {
//            List<Consumo> consumos = consumoRepository.findByDispositivoId(dispositivo.getId());
//            double consumoTotal = consumos.stream().mapToDouble(Consumo::getEnergiaConsumidaKwh).sum();
//
//            promptBuilder.append(String.format("- %s: %.2f kWh consumidos\n", dispositivo.getNome(), consumoTotal));
//        }
//
//        String prompt = promptBuilder.toString();
//
//        List<DicaEconomia> dicasGeradas = new ArrayList<>();
//        try {
//            String respostaIA = chamarOpenAI(prompt);
//
//            String[] dicas = respostaIA.split("\n");
//            for (String dicaTexto : dicas) {
//                if (!dicaTexto.trim().isEmpty()) {
//                    DicaEconomia dica = new DicaEconomia();
//                    dica.setDescricao(dicaTexto.trim());
//                    dica.setCategoria("Gerado por IA");
//                    dica.setRelevancia("Alta");
//                    dicaEconomiaRepository.save(dica);
//                    dicasGeradas.add(dica);
//                }
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//
//        return ResponseEntity.ok(dicasGeradas);
//    }

//    private String chamarOpenAI(String prompt) throws Exception {
//        String apiKey = "SUA_CHAVE_OPENAI";
//        OkHttpClient client = new OkHttpClient();
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "{\"model\":\"text-davinci-003\",\"prompt\":\"" + prompt + "\",\"max_tokens\":100}");
//        Request request = new Request.Builder()
//                .url("https://api.openai.com/v1/completions")
//                .post(body)
//                .addHeader("Authorization", "Bearer " + apiKey)
//                .addHeader("Content-Type", "application/json")
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                return new JSONObject(response.body().string())
//                        .getJSONArray("choices")
//                        .getJSONObject(0)
//                        .getString("text")
//                        .trim();
//            } else {
//                throw new Exception("Erro ao chamar OpenAI");
//            }
//        }
//    }
}
