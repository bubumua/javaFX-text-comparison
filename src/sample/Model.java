package sample;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过调用内部方法，实现两个文本的相似度比较
 *
 * @author Uzedo
 */
public class Model {
    /**
     * 哈希表，用于存放特征向量
     */
    HashMap<String, Integer> allWords, pWords, qWords;
    /**
     * 正则表达式，用于匹配文本中的数值、单词
     */
    public static Pattern wordPattern = Pattern.compile("\\d+.\\d+|\\w+('\\w+)?");
    
    public Model() {
        allWords = new HashMap<>();
        pWords = new HashMap<>();
        qWords = new HashMap<>();
    }
    
    /**
     * 根据给定特征选择和向量距离计算方式选择，计算两个输入的字符串的相似度，并返回结果
     *
     * @param strA         字符串A
     * @param strB         字符串B
     * @param feature      特征
     * @param distanceType 向量距离计算方式
     * @Return double
     * @author Bubu
     */
    public double compareByType(String strA, String strB, int feature, int distanceType) {
        allWords = createAllWords(strA, strB);
        // 生成特征向量
        switch (feature) {
            // 生成词频向量
            case 0:
                pWords = createFrequencyVector(allWords, strA);
                qWords = createFrequencyVector(allWords, strB);
                break;
            // 生成首次出现向量
            case 1:
                pWords = createFirstShowVector(allWords, strA);
                qWords = createFirstShowVector(allWords, strB);
                break;
            default:
                break;
        }
        // 计算距离/相似度
        double similarity = 0;
        switch (distanceType) {
            // 余弦相似度
            case 0:
                similarity = computeCosineDistance(pWords, qWords);
                break;
            // 欧式距离
            case 1:
                similarity = computeEuclideanDistance(pWords, qWords);
                break;
            // 汉明距离
            case 2:
                similarity = computeHammingDistance(pWords, qWords);
                break;
            default:
                break;
        }
        return similarity;
    }
    
    /**
     * 计算简化的汉明距离
     *
     * @param p 向量p
     * @param q 向量q
     * @Return double
     * @author Bubu
     */
    private double computeHammingDistance(HashMap<String, Integer> p, HashMap<String, Integer> q) {
        // 安全性检验。若两个向量长度不等，返回-1
        if (p.size() != q.size()) {
            System.out.println("can not compute!");
            return -1;
        }
        int count = 0;
        for (Map.Entry<String, Integer> en : p.entrySet()) {
            String key = en.getKey();
            int a = p.get(key);
            int b = q.get(key);
            count += (a == b ? 1 : 0);
        }
        return (double) count / p.size();
    }
    
    /**
     * 计算欧氏距离
     *
     * @param p 向量p
     * @param q 向量q
     * @Return double
     * @author Bubu
     */
    private double computeEuclideanDistance(HashMap<String, Integer> p, HashMap<String, Integer> q) {
        // 安全性检验。若两个向量长度不等，返回-1
        if (p.size() != q.size()) {
            System.out.println("can not compute!");
            return -1;
        }
        int sum = 0;
        for (Map.Entry<String, Integer> en : p.entrySet()) {
            String key = en.getKey();
            int a = p.get(key);
            int b = q.get(key);
            sum += Math.pow(a - b, 2);
        }
        return Math.sqrt(sum);
    }
    
    /**
     * 计算余弦相似度
     *
     * @param p 向量p
     * @param q 向量q
     * @Return double
     * @author Bubu
     */
    private double computeCosineDistance(HashMap<String, Integer> p, HashMap<String, Integer> q) {
        // 安全性检验。若两个向量长度不等，返回-1
        if (p.size() != q.size()) {
            System.out.println("can not compute!");
            return -1;
        }
        double numerator = 0;
        double denominator1 = 0, denominator2 = 0;
        for (Map.Entry<String, Integer> en : p.entrySet()) {
            String key = en.getKey();
            int a = p.get(key);
            int b = q.get(key);
            numerator += a * b;
            denominator1 += a * a;
            denominator2 += b * b;
        }
        return numerator / (Math.sqrt(denominator1) * Math.sqrt(denominator2));
    }
    
    /**
     * 输入初始的词汇表以及一段字符串，生成首次出现位置的向量
     *
     * @param allWords 初始的词汇表
     * @param str      字符串
     * @Return java.util.HashMap<java.lang.String, java.lang.Integer>
     * @author Bubu
     */
    private HashMap<String, Integer> createFirstShowVector(HashMap<String, Integer> allWords, String str) {
        HashMap<String, Integer> fsv = deepCopyHashMap(allWords);
        int position = 0;
        Matcher ma = wordPattern.matcher(str);
        while (ma.find()) {
            position++;
            String word = ma.group();
            // 若词汇向量中单词尚未出现过，则将其值设置为当前位置
            fsv.replace(word, 0, position);
        }
        System.out.println("FirstShowVector=" + fsv);
        return fsv;
    }
    
    /**
     * 输入初始的词汇表以及一段字符串，生成词频向量
     *
     * @param allWords 初始的词汇表
     * @param str      字符串
     * @Return java.util.HashMap<java.lang.String, java.lang.Integer>
     * @author Bubu
     */
    private HashMap<String, Integer> createFrequencyVector(HashMap<String, Integer> allWords, String str) {
        HashMap<String, Integer> fv = deepCopyHashMap(allWords);
        Matcher ma = wordPattern.matcher(str);
        // 将匹配到的单词插入哈希表
        while (ma.find()) {
            String word = ma.group();
            fv.computeIfPresent(word, (key, value) -> value += 1);
        }
        System.out.println("FrequencyVector=" + fv);
        return fv;
    }
    
    /**
     * 根据给定的两段字符串，构建词汇哈希表，其中包含两个字符串内的所有英文单词，并赋初始值为0
     *
     * @param strA 字符串A
     * @param strB 字符串B
     * @Return java.util.HashMap<java.lang.String, java.lang.Integer> 返回构建的词汇表
     * @author Bubu
     */
    private HashMap<String, Integer> createAllWords(String strA, String strB) {
        HashMap<String, Integer> allWords = new HashMap<>();
        String word;
        // 匹配左侧单词，将未加入词袋单词加入词袋
        Matcher ma = wordPattern.matcher(strA);
        System.out.println("strA: ");
        while (ma.find()) {
            word = ma.group();
            allWords.put(word, 0);
            System.out.print(word + " ");
        }
        System.out.print("\n");
        // 匹配右侧单词，将未加入词袋单词加入词袋
        ma = wordPattern.matcher(strB);
        System.out.println("strB: ");
        while (ma.find()) {
            word = ma.group();
            allWords.put(word, 0);
            System.out.print(word + " ");
        }
        System.out.print("\n");
        System.out.println("allWords=" + allWords);
        return allWords;
    }
    
    /**
     * 深拷贝一份哈希表
     *
     * @param map 被拷贝的哈希表
     * @Return java.util.HashMap<java.lang.String, java.lang.Integer>
     * @author Bubu
     */
    public static HashMap<String, Integer> deepCopyHashMap(HashMap<String, Integer> map) {
        HashMap<String, Integer> copy = new HashMap<>(map.size());
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }
}
