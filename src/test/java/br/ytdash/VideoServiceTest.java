package br.ytdash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VideoServiceTest {

         @Test
    void biggerJPG16for9Thumbnail() throws Exception {

        // Arrange
        String json = """
        [
            {
                "url": "https://exemplo.com/320.jpg",
                "width": 320,
                "height": 180
            },
            {
                "url": "https://exemplo.com/640.jpg",
                "width": 640,
                "height": 360
            },  
            {
                "url": "https://exemplo.com/720.webp",
                "width": 1280,
                "height": 720
            },
            {
                "url": "https://exemplo.com/720.jpg?",
                "width": 1280,
                "height": 720
            }
        ]
        """;

        ObjectMapper mapper = new ObjectMapper();
        JsonNode thumbnails = mapper.readTree(json);

        VideoService videoService = new VideoService();

        // Act
        String resultado =
            videoService.getThumbnailJPG(thumbnails);

        // Assert
        assertEquals(
            "https://exemplo.com/640.jpg",
            resultado
        );
    }


}
