package test;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FdfsTest {

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private ThumbImageConfig imageConfig;

    @Test
    public void test_Upload() throws FileNotFoundException {
        File file = new File("D:/data/bd_logo1.png");
        // 上传并且生成缩略图
        StorePath storePath = this.client.uploadFile(new FileInputStream(file), file.length(), "png", null);
        // 带分组的路径
        System.out.println(storePath.getFullPath());
        // 不带分组的路径
        System.out.println(storePath.getPath());
    }

    @Test
    public void testUpload() throws FileNotFoundException {
        File file = new File("D:/data/bd_logo1.png");
        // 上传并且生成缩略图
        StorePath storePath = this.client.uploadImageAndCrtThumbImage(
                new FileInputStream(file), file.length(), "png", null);
        // 带分组的路径
        System.out.println(storePath.getFullPath());
        // 不带分组的路径
        System.out.println(storePath.getPath());
        // 获取缩略图路径
        String path = imageConfig.getThumbImagePath(storePath.getPath());
        System.out.println(path);
    }

}