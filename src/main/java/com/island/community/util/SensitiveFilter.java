package com.island.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger looger= LoggerFactory.getLogger(SensitiveFilter.class);

    //设置替换符
    private static final String REPLACEMENT="***";

    //初始化跟结点
    private TrieNode rootNode=new TrieNode();

    public void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for(int i=0;i<keyword.length();i++){
            Character c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if(subNode==null){
                //初始化子结点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            //指向子结点，进入下一轮循环
            tempNode = subNode;

            //设置结束标示
            if(i==keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }


    public boolean isSymbol(Character c){
        //判断是否为特殊字符,且c在东亚文字范围之外（原因是部分东亚文字也可能被纳入在了特殊符号中）
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0x9FFF);
    }

    //过滤敏感词，参数为待过滤文本
    public String fillter(String text){
        if(text == null){
            return null;
        }

        //指针1 指向树的结点指针
        TrieNode tempNode = rootNode;
        //指针2 指向字符串首位
        int begin = 0;
        //指针3 指向
        int position = 0;
        //过滤结果,使用变长字符串
        StringBuilder stringBuilder = new StringBuilder();

        //最上层决定算法结束与否，指针是否已到结尾（最后一个词已经检查完成）
        while (position<text.length()){
            char c =text.charAt(position);

            //判断目前文字是否为特殊符号
            if(isSymbol(c)){
                //若指针1处于跟结点(一个词判断的第一号位)，将此符号计入结果中，指针2向下走一步
                if(tempNode==rootNode){
                    stringBuilder.append(c);
                    begin++;
                }
                //无论特殊符号是在开头还是在中间,指针3都向下走一步
                position++;
                continue;
            }

            tempNode=tempNode.getSubNode(c);
            if(tempNode==null){ //以begin开头的字符串不是敏感词
                stringBuilder.append(text.charAt(begin));
                //进入下一个词的判定
                position=++begin;
                //重新指向跟结点
                tempNode=rootNode;
            }else if(tempNode.isKeywordEnd()){//发现敏感词 begin - position位置的字符
                //替换字符
                stringBuilder.append(REPLACEMENT);
                //进入下一个位置
                begin=++position;
            }else { //未到达跟结点，需要继续检查下一个字符
                position++;
            }
        }
        //将最后一批字符计入到结果（因为下一刻循环会直接结束）
        stringBuilder.append(text.substring(begin));
        return  stringBuilder.toString();
    }

    @PostConstruct //当容器实例化此bean时，将会自动调用此方法
    public void init(){
        //获取类加载器，目的是读取在resources下的配置文件,target.classes.resources.sensitive-words.txt
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String keyWord;
            while((keyWord=reader.readLine())!=null){
                //将读到的敏感词添加到前缀树中
                this.addKeyword(keyWord);
            }

        }catch(IOException e){
            looger.error("加载敏感词失败"+e.getMessage());
        }
    }


    //定义内部类（数据结构）描述前缀树结点
    private class TrieNode{

        //是否为叶子结点
        private boolean isKeywordEnd =false;

        //各结点的子结点(key为下级字符，value为下级结点)
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        //添加子结点
        public void addSubNode(Character c , TrieNode node){
            subNodes.put(c,node);
        }

        //获取子结点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }

        //将铭感词添加到前缀树中

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }


    }
}
