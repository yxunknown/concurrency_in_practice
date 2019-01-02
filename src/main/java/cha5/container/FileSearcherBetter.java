package cha5.container;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class FileSearcherBetter {
    public static void main(String[] args) {
        int cpus = Runtime.getRuntime().availableProcessors();

        final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(cpus);

        BlockingQueue<File> fileQueue = new LinkedBlockingDeque<>(50);
        FileFilter filter = (file) -> file.isDirectory() || file.getName().endsWith(".jpg");
        File root = new File("/Users/hercat/");
        Set<File> indexedFiles = new HashSet<>();
        fixedThreadPool.execute(new FileSearcherWorker(fileQueue, filter, root, indexedFiles, fixedThreadPool));
        new Thread(new Indexer(fileQueue)).start();
        new Thread(new Indexer(fileQueue)).start();
        new Thread(new Indexer(fileQueue)).start();
        new Thread(new Indexer(fileQueue)).start();
        new Thread(new Indexer(fileQueue)).start();
    }
}


class FileSearcherWorker implements Runnable {
    private final BlockingQueue<File> fileQueue;
    private final FileFilter fileFilter;
    private final File directory;
    private final Set<File> indexedFiles;
    private final ExecutorService pool;

    public FileSearcherWorker(BlockingQueue<File> fileQueue,
                              FileFilter fileFilter,
                              File directory,
                              Set<File> indexedFiles,
                              ExecutorService pool) {
        this.fileQueue = fileQueue;
        this.fileFilter = fileFilter;
        this.directory = directory;
        this.indexedFiles = indexedFiles;
        this.pool = pool;
    }

    @Override
    public void run() {
        try {
            searchDirectory(directory);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void searchDirectory(File directory) throws InterruptedException {
        File[] files = directory.listFiles(fileFilter);
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    pool.execute(new FileSearcherWorker(fileQueue, fileFilter, f, indexedFiles, pool));
                } else if (!isFileIndexed(f)) {
                    fileQueue.put(f);
                }
            }
        }
    }

    private boolean isFileIndexed(File f) {
        synchronized (indexedFiles) {
            if (indexedFiles.contains(f)) {
                return true;
            } else {
                indexedFiles.add(f);
                return false;
            }
        }
    }
}
