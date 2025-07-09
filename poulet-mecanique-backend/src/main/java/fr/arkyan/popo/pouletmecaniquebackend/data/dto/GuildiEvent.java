package fr.arkyan.popo.pouletmecaniquebackend.data.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
public class GuildiEvent {

    private static final String URL_REGEX = "raidplanner\\/([^\\/]+)\\/([^\\/]+)";

    private String id;
    private String color;
    private String start;
    private String end;
    private String textColor;
    private String title;
    private String category;

    @Setter(AccessLevel.NONE)
    private String url;

    public void setUrl(String url){
        this.url = url;
        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            this.category = matcher.group(1);
            this.id = matcher.group(2);
        }
    }

}
