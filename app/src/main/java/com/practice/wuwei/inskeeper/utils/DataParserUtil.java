package com.practice.wuwei.inskeeper.utils;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.practice.wuwei.inskeeper.InsSource;

/**
 * @author by wuwei
 * @date on 2018/2/9 下午4:44
 */

public class DataParserUtil {

    public static InsSource praseSourceXML(String xml) {

        String jsonString = getJsonFromXML(xml);
        if (jsonString.isEmpty()) {
            return null;
        }
        return getInsSourceFromJson(jsonString);
    }

    private static String getJsonFromXML(String xml) {
        if (xml == null || xml.isEmpty()) {
            return "";

        }
        String tmp1 = xml.substring(xml.indexOf("window._sharedData = "));
        Log.e("tmp1",tmp1);
        String tmp2 = tmp1.substring(0,tmp1.indexOf(";</script>"));
        Log.e("tmp2",tmp2);
        String tmp3 = tmp2.replace("window._sharedData = ","");
        Log.e("tmp3",tmp3);
        return tmp3;
    }

    private static InsSource getInsSourceFromJson(String jsonString) {
        InsSource insSource = null;
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObjectEnttryData = jsonParser.parse(jsonString).getAsJsonObject().getAsJsonObject("entry_data");
            JsonArray jsonArrayPostPages = jsonObjectEnttryData.getAsJsonArray("PostPage");
            JsonObject jsonObjectGraphql = ((JsonObject) jsonArrayPostPages.get(0)).getAsJsonObject("graphql");
            JsonObject jsonObjectShortcodeMedia = jsonObjectGraphql.getAsJsonObject("shortcode_media");

            String display_url = jsonObjectShortcodeMedia.get("display_url").getAsString();
            boolean is_video = jsonObjectShortcodeMedia.get("is_video").getAsBoolean();
            String video_url = null;
            if (is_video) {
                video_url = jsonObjectShortcodeMedia.get("video_url").getAsString();
            }
            String[] urls = null;
            if (jsonObjectShortcodeMedia.getAsJsonObject("edge_sidecar_to_children") != null) {
                JsonArray jsonArrayEdges = jsonObjectShortcodeMedia.getAsJsonObject("edge_sidecar_to_children").getAsJsonArray("edges");
                JsonObject jsonObjectNode = null;
                urls = new String[jsonArrayEdges.size()];
                for (int i = 0; i < jsonArrayEdges.size(); i++) {
                    jsonObjectNode = ((JsonObject) jsonArrayEdges.get(i)).getAsJsonObject("node");
                    urls[i] = jsonObjectNode.get("display_url").getAsString();
                }
            }
            insSource = new InsSource();
            insSource.display_url = display_url;
            insSource.is_video = is_video;
            insSource.urls = urls;
            insSource.video_url = video_url;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.gc();
            return insSource;
        }
    }
}
