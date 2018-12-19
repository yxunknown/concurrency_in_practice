package cha5.container;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class FileSearcher {
    public static void main(String[] args) {
        BlockingQueue<File> fileQueue = new LinkedBlockingDeque<>(50);
        FileFilter filter = (file) -> file.isDirectory() ||
            file.getName().endsWith(".jpg") ||
            file.getName().endsWith(".png");

        File root = new File("/Users/hercat/");
        Set<File> indexedFiles = new HashSet<>();
        new Thread(new FileCrawler(fileQueue, filter, root, indexedFiles)).start();
        int consumers = 10;
        for (int i = 0; i < consumers; i++) {
            new Thread(new Indexer(fileQueue)).start();
        }

    }
}


class FileCrawler implements Runnable {
    private final BlockingQueue<File> fileQueue;
    private final FileFilter fileFilter;
    private final File root;
    private final Set<File> indexedFiles;

    public FileCrawler(BlockingQueue<File> fileQueue, FileFilter fileFilter, File root, Set<File> indexedFiles) {
        this.fileQueue = fileQueue;
        this.fileFilter = fileFilter;
        this.root = root;
        this.indexedFiles = indexedFiles;
    }

    @Override
    public void run() {
        try {
            crawl(root);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void crawl(File root) throws InterruptedException {
        File[] entries = root.listFiles(fileFilter);
        if (entries != null) {
            for (File entry : entries) {
                if (entry.isDirectory()) {
                    // start new thread to search
                    new Thread(new FileCrawler(fileQueue, fileFilter, entry, indexedFiles)).start();
                } else if (!isAlreadyIndexed(entry)) {
                    fileQueue.put(entry);
                }
            }
        }
    }

    private boolean isAlreadyIndexed(File file) {
        synchronized (indexedFiles) {
            if (indexedFiles.contains(file)) {
                return true;
            } else {
                indexedFiles.add(file);
                return false;
            }
        }
    }
}

class Indexer implements Runnable {
    private final BlockingQueue<File> queue;

    public Indexer(BlockingQueue<File> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                File aFile = queue.poll(3000, TimeUnit.MILLISECONDS);
                if (aFile == null) {
                    // no more file matched
                    System.out.println("search done!!!");
                    break;
                } else {
                    index(aFile);
                }

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void index(File file) {
        System.out.println("find file: " + file);
    }
}