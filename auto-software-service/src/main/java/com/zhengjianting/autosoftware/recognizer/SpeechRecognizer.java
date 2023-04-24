package com.zhengjianting.autosoftware.recognizer;

import com.alibaba.fastjson.JSONPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
public class SpeechRecognizer {

    private String appkey;
    private String accessToken;

    private String processFile(String fileName, String format, int sampleRate,
                        boolean enablePunctuationPrediction,
                        boolean enableInverseTextNormalization,
                        boolean enableVoiceDetection) {

        /**
         * 设置HTTPS RESTful POST请求：
         * 1.使用HTTPS协议。
         * 2.语音识别服务域名：nls-gateway-cn-shanghai.aliyuncs.com。
         * 3.语音识别接口请求路径：/stream/v1/asr。
         * 4.设置必选请求参数：appkey、format、sample_rate。
         * 5.设置可选请求参数：enable_punctuation_prediction、enable_inverse_text_normalization、enable_voice_detection。
         */
        String url = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/asr";
        String request = url;
        request = request + "?appkey=" + appkey;
        request = request + "&format=" + format;
        request = request + "&sample_rate=" + sampleRate;
        if (enablePunctuationPrediction) {
            request = request + "&enable_punctuation_prediction=" + true;
        }
        if (enableInverseTextNormalization) {
            request = request + "&enable_inverse_text_normalization=" + true;
        }
        if (enableVoiceDetection) {
            request = request + "&enable_voice_detection=" + true;
        }

        System.out.println("Request: " + request);

        /**
         * 设置HTTPS头部字段：
         * 1.鉴权参数。
         * 2.Content-Type：application/octet-stream。
         */
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-NLS-Token", this.accessToken);
        headers.put("Content-Type", "application/octet-stream");

        /**
         * 发送HTTPS POST请求，返回服务端的响应。
         */
        String response = HttpUtil.sendPostFile(request, headers, fileName);

        if (response != null) {
            System.out.println("Response: " + response);
            String result = JSONPath.read(response, "result").toString();
            System.out.println("识别结果：" + result);
            return result;
        }
        else {
            System.err.println("识别失败!");
            return null;
        }
    }

    private String processData(byte[] data, String format, int sampleRate,
                               boolean enablePunctuationPrediction,
                               boolean enableInverseTextNormalization,
                               boolean enableVoiceDetection) {

        String url = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/asr";
        String request = url;
        request = request + "?appkey=" + appkey;
        request = request + "&format=" + format;
        request = request + "&sample_rate=" + sampleRate;
        if (enablePunctuationPrediction) {
            request = request + "&enable_punctuation_prediction=" + true;
        }
        if (enableInverseTextNormalization) {
            request = request + "&enable_inverse_text_normalization=" + true;
        }
        if (enableVoiceDetection) {
            request = request + "&enable_voice_detection=" + true;
        }

        System.out.println("Request: " + request);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-NLS-Token", this.accessToken);
        headers.put("Content-Type", "application/octet-stream");

        return HttpUtil.sendPostData(request, headers, data);
    }

    @PostMapping("/speechRecognition")
    public String speechRecognition(@RequestBody byte[] data) {
        appkey = "hLjHvrvGmwYHly4J";
        accessToken = CreateToken.token();

        String errorMessage = "语音识别出错, 请联系管理员";
        if (!StringUtils.hasLength(accessToken)) {
            return errorMessage;
        }

        String format = "pcm";
        int sampleRate = 16000;
        boolean enablePunctuationPrediction = true;
        boolean enableInverseTextNormalization = true;
        boolean enableVoiceDetection = false;

        String result = processData(data, format, sampleRate, enablePunctuationPrediction, enableInverseTextNormalization, enableVoiceDetection);
        if (!StringUtils.hasLength(result)) {
            return errorMessage;
        }

        return result;
    }
}
