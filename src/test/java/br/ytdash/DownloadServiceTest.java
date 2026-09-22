package br.ytdash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DownloadServiceTest {

@Test
void extractPercentTest () throws Exception {

    //Arange
    String line = "testetesteteste [nseioq] 67%lalalala";

    //Act
    String resultado = (DownloadService.extractPercent(line));

    //Assert
    assertEquals(67, Integer.parseInt(resultado));

}
}