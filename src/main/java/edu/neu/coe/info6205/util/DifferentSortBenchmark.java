package edu.neu.coe.info6205.util;

import edu.neu.coe.info6205.sort.BaseHelper;
import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.HelperFactory;
import edu.neu.coe.info6205.sort.InstrumentedHelper;
import edu.neu.coe.info6205.sort.elementary.HeapSort;
import edu.neu.coe.info6205.sort.linearithmic.MergeSort;
import edu.neu.coe.info6205.sort.linearithmic.QuickSort_DualPivot;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import static edu.neu.coe.info6205.util.SortBenchmark.timeLoggersLinearithmic;
import static java.util.concurrent.CompletableFuture.runAsync;

public class DifferentSortBenchmark {
    public static void main(String[] args) {
        try {
            File heapFile = new File("HeapBenchmark.csv");
            File mergeFile = new File("MergeBenchmark.csv");
            File quickFile = new File("QuickBenchmark.csv");
            heapFile.createNewFile();
            quickFile.createNewFile();
            mergeFile.createNewFile();
            FileWriter heapFileWriter = new FileWriter(heapFile);
            FileWriter quickFileWriter = new FileWriter(quickFile);
            FileWriter mergeFileWriter = new FileWriter(mergeFile);
            heapFileWriter.write(getHeaderString());
            quickFileWriter.write(getHeaderString());
            mergeFileWriter.write(getHeaderString());
            boolean instrumentation = false;

            System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());
            Config config = Config.setupConfig("false", "", "0", "","");

            int starting = 10000;
            int ending = 160000;

            CompletableFuture<FileWriter> heapSort = runHeapSort(starting, ending, config, heapFileWriter);
            heapSort.join();
            CompletableFuture<FileWriter> quickSort = runQuickSort(starting, ending, config, quickFileWriter);
            quickSort.join();
            CompletableFuture<FileWriter> mergeSort = runMergeSort(starting, ending, config, mergeFileWriter);
            mergeSort.join();
        } catch (Exception e) {
            System.out.println("Error occurred while sorting" + e);
        }
    }

    public static  CompletableFuture runHeapSort(int starting, int ending, Config config, FileWriter fw) {
        return runAsync(
                () -> {
                    for (int i = starting; i <= ending; i *= 2) {
                        Helper<Integer> helper = HelperFactory.create("HeapSort", i, config);
                        HeapSort<Integer> sorting = new HeapSort<>(helper);
                        final int value = i;
                        Integer[] array = helper.random(Integer.class, r -> r.nextInt(value));
                        SorterBenchmark sb = new SorterBenchmark<>(Integer.class, (Integer[] intArray) -> {
                            for (int x = 0; x < intArray.length; x++) {
                                intArray[x] = intArray[x];
                            }
                            return intArray;
                        },
                                sorting, array, 5, timeLoggersLinearithmic);
                        double time = sb.run(i);
                        try {
                            if (helper instanceof BaseHelper) {
                                fw.write(createCSVString(i, time, null, config.isInstrumented()));
                            } else {
                                fw.write(createCSVString(i, time, ((InstrumentedHelper) helper).getStatPack(), config.isInstrumented()));
                            }
                        } catch (Exception e) {
                            System.out.println("Error while writing heap file" + e);
                        }
                    }
                    try {
                        fw.flush();
                        fw.close();
                    } catch (Exception e) {
                        System.out.println("Error while closing heap file." + e);
                    }
                }
        );
    }

    private static CompletableFuture runMergeSort(int starting, int ending, Config config, FileWriter fw) {
        return runAsync(
                () -> {
                    for (int n = starting; n <= ending; n *= 2) {
                        Helper<Integer> helper = HelperFactory.create("MergeSort", n, config);
                        MergeSort<Integer> sort = new MergeSort<>(helper);
                        final int value = n;
                        Integer[] arr = helper.random(Integer.class, r -> r.nextInt(value));
                        SorterBenchmark sorterBenchmark = new SorterBenchmark<>(Integer.class,
                                (Integer[] array) -> {
                                    for (int i = 0; i < array.length; i++) {
                                        array[i] = array[i];
                                    }
                                    return array;
                                },
                                sort, arr, 5, timeLoggersLinearithmic);
                        double time = sorterBenchmark.run(n);
                        try {
                            if (helper instanceof BaseHelper) {
                                fw.write(createCSVString(n, time, null, config.isInstrumented()));
                            } else
                            {
                                fw.write(createCSVString(n, time, ((InstrumentedHelper) helper).getStatPack(), config.isInstrumented()));
                            }
                        } catch (Exception e) {
                            System.out.println("Error while writing merge file" + e);
                        }
                    }
                    try {
                        fw.flush();
                        fw.close();
                    } catch (Exception e) {
                        System.out.println("error while closing merge file" + e);
                    }
                }
        );
    }

    private static CompletableFuture runQuickSort(int starting, int ending, Config config, FileWriter fw) {
        return runAsync(
                () -> {
                    for (int n = starting; n <= ending; n *= 2) {
                        Helper<Integer> helper = HelperFactory.create("QuickSort", n, config);
                        QuickSort_DualPivot<Integer> sort = new QuickSort_DualPivot<>(helper);
                        final int value = n;
                        Integer[] array = helper.random(Integer.class, r -> r.nextInt(value));
                        SorterBenchmark sorterBenchmark = new SorterBenchmark<>(Integer.class,
                                (Integer[] intArray) -> {
                                    for (int i = 0; i < intArray.length; i++) {
                                        intArray[i] = intArray[i];
                                    }
                                    return intArray;
                                },
                                sort, array, 5, timeLoggersLinearithmic);
                        double time = sorterBenchmark.run(n);
                        try {
                            if (helper instanceof BaseHelper) {
                                fw.write(createCSVString(n, time, null, config.isInstrumented()));
                            } else {
                                fw.write(createCSVString(n, time, ((InstrumentedHelper) helper).getStatPack(), config.isInstrumented()));
                            }
                        } catch (Exception e) {
                            System.out.println("error while writing file Quick" + e);
                        }
                    }
                    try {
                        fw.flush();
                        fw.close();
                    } catch (Exception e) {
                        System.out.println("error while closing file Quick" + e);
                    }
                }
        );
    }

    public final static TimeLogger[] timeLoggersLinearithmic = {
            new TimeLogger("Raw time per run (mSec): ", (time, n) -> time)
    };

    private static String createCSVString(int i, double time, StatPack sp, boolean instru) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i + ",");
        stringBuilder.append(time + ",");

        if(instru) {
            stringBuilder.append(sp.getStatistics("hits").mean() + ",");
            stringBuilder.append(sp.getStatistics("hits").stdDev() + ",");
            stringBuilder.append(sp.getStatistics("hits").normalizedMean() + ",");
            stringBuilder.append(sp.getStatistics("swaps").mean() + ",");
            stringBuilder.append(sp.getStatistics("swaps").stdDev() + ",");
            stringBuilder.append(sp.getStatistics("swaps").normalizedMean() + ",");
            stringBuilder.append(sp.getStatistics("compares").mean() + ",");
            stringBuilder.append(sp.getStatistics("compares").stdDev() + ",");
            stringBuilder.append(sp.getStatistics("fixes").mean() + ",");
            stringBuilder.append(sp.getStatistics("fixes").stdDev() + ",");
            stringBuilder.append(sp.getStatistics("fixes").normalizedMean() + "\n");
        } else {
            stringBuilder.append("\n");
        }
        System.out.println();
        return stringBuilder.toString();
    }

    private static String getHeaderString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("N,");
        stringBuilder.append("Time,");
        stringBuilder.append("hits:Mean,");
        stringBuilder.append("hits:StdDev,");
        stringBuilder.append("hits:NormalizedMean,");
        stringBuilder.append("swaps:Mean,");
        stringBuilder.append("swaps:StdDev,");
        stringBuilder.append("swaps:NormalizedMean,");
        stringBuilder.append("compares:Mean,");
        stringBuilder.append("compares:StdDev,");
        stringBuilder.append("compares:NormalizedMean,");
        stringBuilder.append("fixes:Mean,");
        stringBuilder.append("fixes:StdDev,");
        stringBuilder.append("fixes:NormalizedMean\n");

        return stringBuilder.toString();

    }

}
