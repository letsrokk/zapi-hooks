package org.fxclub.qa.zapi;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class ZapiFileLock {

    private Logger logger = LoggerFactory.getLogger(ZapiFileLock.class);

    private File file;
    private FileLock fileLock;

    public ZapiFileLock(String name){
        this.file = new File("target/" + name + ".lock");
        try{
            this.file.createNewFile();
        }catch (IOException e){
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void lock(){
        try{
            logger.debug("ZAPI FILE LOCK: LOCK");
            fileLock = new RandomAccessFile(file, "rw").getChannel().lock();
        }catch (IOException e){
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void unlock(){
        try {
            logger.debug("ZAPI FILE LOCK: UNLOCK");
            fileLock.release();
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
