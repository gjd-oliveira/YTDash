package br.ytdash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
                "url": "https://exemplo.com/1280.webp",
                "width": 1280,
                "height": 720
            },
            {
                "url": "https://exemplo.com/1280.jpg?",
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

@Test
void mustFindShortsThumbnail() throws Exception {

    // Arrange
    String json = """
    [
        {
            "url": "https://exemplo.com/180.jpg",
            "width": 180,
            "height": 320
        },
        {
            "url": "https://exemplo.com/360.jpg",
            "width": 360,
            "height": 640
        },
        {
            "url": "https://exemplo.com/720.webp",
            "width": 720,
            "height": 1280
        },
        {
            "url": "https://exemplo.com/720.jpg?",
            "width": 720,
            "height": 1280
        },
        {
            "url": "https://i.ytimg.com/vi/pYCySFq4vLg/maxresdefault.jpg",
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
    "https://i.ytimg.com/vi/pYCySFq4vLg/maxresdefault.jpg",
    resultado
);

}
}