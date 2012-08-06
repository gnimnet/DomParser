package net.gnim.extend;

import java.io.*;
import java.util.ArrayList;

/**
 * DOM解析器
 *
 * @author ming
 * @version 1.1.0
 */
public class DomParser {

    /**
     * 特殊标签，内HTML代码不能被解析
     */
    private static final String[] SPECIAL_TAG = {"style", "script"};
    /**
     * 特殊标签，无内HTML代码，不与结束标签匹配
     */
    private static final String[] SINGLE_TAG = {"br", "hr", "img", "input", "param", "meta", "link", "area"};
    /**
     * 默认缩进
     */
    private static final String DEFAULT_SPACE = "  ";
    /**
     * 默认换行
     */
    private static final String DEFAULT_LINE = "\n";

    /**
     * 节点
     */
    public static class Node {

        /**
         * 节点的过滤器
         */
        public static interface Filter {

            /**
             * 判断节点是否满足过滤条件
             *
             * @param node 判断节点
             * @return 是否满足条件
             */
            public boolean match(Node node);
        }

        /**
         * 元素过滤器
         */
        public static interface FilterElement {

            /**
             * 判断节点是否满足过滤条件
             *
             * @param node 判断节点
             * @return 是否满足条件
             */
            public boolean match(NodeElement node);
        }
        /**
         * 父节点引用
         */
        public Node parent;
        /**
         * 子节点组
         */
        public ArrayList<Node> children;

        /**
         * 获取父节点
         *
         * @return 父节点
         */
        public Node getParent() {
            return parent;
        }

        /**
         * 获取子节点组
         *
         * @return 子节点组
         */
        public ArrayList<Node> getChildren() {
            return children;
        }

        /**
         * 获取子元素节点组
         *
         * @return 子元素节点组
         */
        public ArrayList<NodeElement> getChildElements() {
            if (children == null) {
                return null;
            }
            ArrayList<NodeElement> result = new ArrayList<NodeElement>();
            for (int i = 0; i < children.size(); i++) {
                Node node = children.get(i);
                if (node instanceof NodeElement) {
                    result.add((NodeElement) node);
                }
            }
            return result;
        }

        /**
         * 获取指定子元素节点组
         *
         * @param tagName 标签名称
         * @return 子元素节点组
         */
        public ArrayList<NodeElement> getChildElements(String tagName) {
            if (children == null) {
                return null;
            }
            ArrayList<NodeElement> result = new ArrayList<NodeElement>();
            for (int i = 0; i < children.size(); i++) {
                Node node = children.get(i);
                if (node instanceof NodeElement) {
                    NodeElement element = (NodeElement) node;
                    if (element.name.equals(tagName)) {
                        result.add(element);
                    }
                }
            }
            return result;
        }

        /**
         * 获取指定索引的子节点
         *
         * @param index 子节点所在索引
         * @return 子节点
         */
        public Node getChild(int index) {
            return (children != null && children.size() >= index) ? children.get(index) : null;
        }

        /**
         * 获取指定索引的元素子节点
         *
         * @param index 子节点所在索引
         * @return 元素子节点
         */
        public NodeElement getChildElement(int index) {
            if (children != null) {
                int count = 0;
                for (int i = 0; i < children.size(); i++) {
                    if (children.get(i) instanceof NodeElement) {
                        if (count == index) {
                            return (NodeElement) children.get(i);
                        }
                        count++;
                    }
                }
            }
            return null;
        }

        /**
         * 查找节点所在子节点索引
         *
         * @param child 节点
         * @return 节点所在子节点索引
         */
        public int indexOfChild(Node child) {
            if (children != null && child != null) {
                for (int i = 0; i < children.size(); i++) {
                    if (children.get(i).equals(child)) {
                        return i;
                    }
                }
            }
            return -1;
        }

        /**
         * 查找节点所在子节点索引
         *
         * @param child 节点
         * @return 节点所在子节点索引
         */
        public int indexOfChildElement(NodeElement child) {
            if (children != null && child != null) {
                int index = 0;
                for (int i = 0; i < children.size(); i++) {
                    Node node = children.get(i);
                    if (node instanceof NodeElement) {
                        if (node.equals(child)) {
                            return index;
                        }
                        index++;
                    }
                }
            }
            return -1;
        }

        /**
         * 设置父节点
         *
         * @param newParent 新父节点
         * @return 老父节点
         */
        public Node setParent(Node newParent) {
            Node oldParent = parent;
            if (oldParent != null) {
                oldParent.removeChild(this);
            }
            if (newParent != null) {
                newParent.addChild(this);
            } else {
                parent = null;
            }
            return oldParent;
        }

        /**
         * 添加子节点
         *
         * @param child 子节点
         * @return 类本身
         */
        public Node addChild(Node child) {
            if (children == null) {
                children = new ArrayList<Node>();
            }
            child.parent = this;
            children.add(child);
            return this;
        }

        /**
         * 添加子节点
         *
         * @param child 子节点
         * @param index 子节点位置索引
         * @return 类本身
         */
        public Node addChild(Node child, int index) {
            if (children == null) {
                children = new ArrayList<Node>();
            }
            child.parent = this;
            if (index < 0 || index > children.size()) {
                children.add(child);
            } else {
                children.add(index, child);
            }
            return this;
        }

        /**
         * 移除子节点
         *
         * @param child 子节点
         * @return 是否移除成功
         */
        public boolean removeChild(Node child) {
            if (children != null && child != null) {
                for (int i = 0; i < children.size(); i++) {
                    if (child.equals(children.get(i))) {
                        children.remove(i);
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * 获取内部DOM
         *
         * @return 内部DOM字符串
         */
        public String inner() {
            if (children != null) {
                StringBuilder sb = new StringBuilder();
                for (Node child : children) {
                    sb.append(child.toString());//打印子节点
                }
                return sb.toString();
            } else {
                return "";
            }
        }

        /**
         * 设定内部Dom
         *
         * @param document 文档字符串
         * @return 是否设定成功
         */
        public boolean inner(String document) {
            return inner(document, false);
        }

        /**
         * 设定内部Dom
         *
         * @param document 文档字符串
         * @param xmlmode 是否以XML解析，区分大小写，不去除特殊标签（script/style）
         * @return 是否设定成功
         */
        public boolean inner(String document, boolean xmlmode) {
            Node root = build(document, xmlmode);
            if (root.children != null) {
                children.clear();
                for (Node child : root.children) {
                    addChild(child);
                }
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return toString(false, DEFAULT_SPACE, DEFAULT_LINE, 0);
        }

        /**
         * 转为字符串
         *
         * @param format 是否执行格式化
         * @return 转为字符串的节点
         */
        public String toString(boolean format) {
            return toString(format, DEFAULT_SPACE, DEFAULT_LINE, 0);
        }

        /**
         * 转为字符串
         *
         * @param format 是否执行格式化
         * @param space 缩进字符串
         * @return 转为字符串的节点
         */
        public String toString(boolean format, String space) {
            return toString(format, space, DEFAULT_LINE, 0);
        }

        /**
         * 转为字符串
         *
         * @param format 是否执行格式化
         * @param space 缩进字符串
         * @param line 换行字符串
         * @return 转为字符串的节点
         */
        public String toString(boolean format, String space, String line) {
            return toString(format, space, line, 0);
        }

        /**
         * 转为字符串
         *
         * @param format 是否执行格式化
         * @param space 缩进字符串
         * @param line 换行字符串
         * @param levelref 参照缩进层级
         * @return 转为字符串的节点
         */
        public String toString(boolean format, String space, String line, int levelref) {
            if (children != null) {
                StringBuilder sb = new StringBuilder();
                if (format) {
                    int childLevel = getLevel() + levelref + 1;
                    int count = 0;
                    for (Node child : children) {
                        if (count > 0) {
                            sb.append(line);//打印换行
                        }
                        for (int i = 0; i < childLevel; i++) {
                            sb.append(space);//打印缩进
                        }
                        sb.append(child.toString(format, space, line, levelref));//打印子节点
                        count++;
                    }
                } else {
                    for (Node child : children) {
                        sb.append(child.toString(format, space, line, levelref));//打印子节点
                    }
                }
                return sb.toString();
            } else {
                return "";
            }
        }

        /**
         * 通过过滤器查找节点
         *
         * @param filter 过滤器
         * @return 满足过滤条件的节点组
         */
        public ArrayList<Node> getNodesByFilter(Filter filter) {
            ArrayList<Node> results = new ArrayList<Node>();
            findNodesByFilter(results, filter, this);
            return results;
        }

        /**
         * 通过过滤器查找子节点
         *
         * @param filter 过滤器
         * @return 满足过滤条件的子节点组
         */
        public ArrayList<Node> getChildNodesByFilter(Filter filter) {
            ArrayList<Node> results = new ArrayList<Node>();
            findChildNodesByFilter(results, filter, this);
            return results;
        }

        /**
         * 通过过滤器查找元素节点
         *
         * @param filter 元素过滤器
         * @return 满足过滤条件的元素节点组
         */
        public ArrayList<NodeElement> getElementsByFilter(FilterElement filter) {
            final FilterElement elmFilter = filter;
            ArrayList<Node> matches = getNodesByFilter(new Filter() {

                @Override
                public boolean match(Node node) {
                    if (node instanceof NodeElement) {
                        return elmFilter.match((NodeElement) node);
                    }
                    return false;
                }
            });
            ArrayList<NodeElement> result = new ArrayList<NodeElement>();
            for (Node node : matches) {
                if (node instanceof NodeElement) {
                    result.add((NodeElement) node);
                }
            }
            return result;
        }

        /**
         * 通过过滤器查找子元素节点
         *
         * @param filter 元素过滤器
         * @return 满足过滤条件的子元素节点组
         */
        public ArrayList<NodeElement> getChildElementsByFilter(FilterElement filter) {
            final FilterElement elmFilter = filter;
            ArrayList<Node> matches = getChildNodesByFilter(new Filter() {

                @Override
                public boolean match(Node node) {
                    if (node instanceof NodeElement) {
                        return elmFilter.match((NodeElement) node);
                    }
                    return false;
                }
            });
            ArrayList<NodeElement> result = new ArrayList<NodeElement>();
            for (Node node : matches) {
                if (node instanceof NodeElement) {
                    result.add((NodeElement) node);
                }
            }
            return result;
        }

        /**
         * 通过ID查找节点
         *
         * @param id 元素ID
         * @return 找到的节点列表
         */
        public ArrayList<NodeElement> getElementById(String id) {
            final String idFinal = id;
            return getElementsByFilter(new FilterElement() {

                @Override
                public boolean match(NodeElement node) {
                    return strEqual(idFinal, node.getAttrValue("id"), false);
                }
            });
        }

        /**
         * 通过类名查找节点
         *
         * @param className 元素类名
         * @return 找到的节点列表
         */
        public ArrayList<NodeElement> getElementsByClassName(String className) {
            final String classNameFinal = className;
            return getElementsByFilter(new FilterElement() {

                @Override
                public boolean match(NodeElement node) {
                    String elementClassName = node.getAttrValue("class");
                    if (elementClassName != null) {
                        String[] classes = elementClassName.split("\\s");
                        for (String cn : classes) {
                            if (strEqual(classNameFinal, cn, false)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
        }

        /**
         * 通过标签名查找节点
         *
         * @param tagName 元素标签名
         * @return 找到的节点列表
         */
        public ArrayList<NodeElement> getElementsByTagName(String tagName) {
            return getElementsByTagName(tagName, false);
        }

        /**
         * 通过标签名查找节点
         *
         * @param tagName 元素标签名
         * @param xmlmode 是否以XML解析，区分大小写
         * @return 找到的节点列表
         */
        public ArrayList<NodeElement> getElementsByTagName(String tagName, boolean xmlmode) {
            final String tagNameFinal = tagName;
            final boolean addAll = "*".equals(tagName);
            final boolean xmlmodeFinal = xmlmode;
            return getElementsByFilter(new FilterElement() {

                @Override
                public boolean match(NodeElement node) {
                    return addAll || strEqual(tagNameFinal, node.name, !xmlmodeFinal);
                }
            });
        }

        /**
         * 通过属性查找元素
         *
         * @param tagAttrName 属性名
         * @param tagAttrValue 属性值
         * @param xmlmode 是否区分大小写
         * @return 找到的节点列表
         */
        public ArrayList<NodeElement> getElementsByTagAttr(String tagAttrName, String tagAttrValue, boolean xmlmode) {
            final String tagAttrNameFinal = tagAttrName;
            final String tagAttrValueFinal = tagAttrValue;
            final boolean xmlmodeFinal = xmlmode;
            return getElementsByFilter(new FilterElement() {

                @Override
                public boolean match(NodeElement node) {
                    return strEqual(tagAttrValueFinal, node.getAttrValue(tagAttrNameFinal, !xmlmodeFinal), false);
                }
            });
        }

        /**
         * 通过过滤器查找元素并添加到结果中
         *
         * @param matches 满足条件的节点组
         * @param filter 过滤器
         * @param context 当前上下文结点
         */
        private static void findNodesByFilter(ArrayList<Node> matches, Filter filter, Node context) {
            if (context.children != null) {
                for (Node child : context.children) {
                    if (filter.match(child)) {
                        matches.add(child);
                    }
                    findNodesByFilter(matches, filter, child);
                }
            }
        }

        /**
         * 通过过滤器查找子元素并添加到结果中
         *
         * @param matches 满足条件的节点组
         * @param filter 过滤器
         * @param context 当前上下文结点
         */
        private static void findChildNodesByFilter(ArrayList<Node> matches, Filter filter, Node context) {
            if (context.children != null) {
                for (Node child : context.children) {
                    if (filter.match(child)) {
                        matches.add(child);
                    }
                }
            }
        }

        /**
         * 通过CSS选择器查找结点组<br/> 支持的选择器：<br/> E<br/>E
         * F<br/>E#F<br/>E.F<br/>E>F<br/>E+F<br/>E~F<br/>
         * E[foo]<br/>E[foo="bar"]<br/>E[foo~="bar"]<br/>
         * E[foo^="bar"]<br/>E[foo$="bar"]<br/>E[foo*="bar"]<br/>E[foo|="bar"]<br/>
         * E:root<br/>E:nth-child(n)<br/>E:nth-last-child(n)<br/>
         * E:nth-of-type(n)<br/>E:nth-last-of-type(n)<br/>
         * E:first-child<br/>E:last-child<br/>E:first-of-type<br/>E:last-of-type<br/>
         * E:only-child<br/>E:only-of-type<br/>E:empty<br/>
         *
         * @param cssSelector css选择器
         * @return 找到的节点列表
         */
        public ArrayList<NodeElement> search(String cssSelector) {
            ArrayList<Node> context = new ArrayList<Node>();
            ArrayList<NodeElement> result = null;
            context.add(this);
            if (cssSelector == null || cssSelector.isEmpty()) {
                return new ArrayList<NodeElement>();
            }
            ArrayList<String> words = getSelectorWordList(cssSelector);
            int index = 0;
            while (index < words.size()) {
                String word = words.get(index);
                if ("#".equals(word) || ".".equals(word) || ">".equals(word)
                        || "+".equals(word) || "~".equals(word)) {
                    if (index + 1 < words.size()) {
                        result = searchElement(context, word.charAt(0), words.get(index + 1));
                        index++;
                    } else {
                        result = null;
                    }
                } else if (":".equals(word)) {
                    if (index + 1 < words.size()) {
                        result = searchElement(context, word.charAt(0), words.get(index + 1));
                        index++;
                    } else {
                        result = null;
                    }
                } else if ("[".equals(word)) {
                    int endIndex = index + 1;
                    while (endIndex < words.size()) {
                        if ("]".equals(words.get(endIndex))) {
                            break;
                        }
                        endIndex++;
                    }
                    if (endIndex < words.size()) {
                        int size = endIndex - index;
                        switch (size) {
                            case 2://[NAME]
                                result = searchElement(context, '[', words.get(index + 1));
                                break;
                            case 4://[NAME=VALUE]
                                result = searchElement(context, '[', words.get(index + 1), ' ', words.get(index + 3));
                                break;
                            case 5://[NAME?=VALUE]
                                result = searchElement(context, '[', words.get(index + 1),
                                        words.get(index + 2).charAt(0), words.get(index + 4));
                                break;
                            default:
                                result = null;
                                break;
                        }
                        index = endIndex + 1;
                    } else {
                        result = null;
                    }
                } else {
                    result = searchElement(context, ' ', word);
                }
                if (result != null) {
                    context = new ArrayList<Node>();
                    for (NodeElement elm : result) {
                        context.add(elm);
                    }
                }
                index++;
            }
            return result;
        }

        /**
         * 获取选择器单词链
         *
         * @param cssSelector 选择器字符串
         * @return 选择器单词链
         */
        private static ArrayList<String> getSelectorWordList(String cssSelector) {
            ArrayList<String> words = new ArrayList<String>();
            char[] splitCh = " #.*:^$|>+~=[]\t\r\n".toCharArray();
            StringBuilder buff = new StringBuilder();
            int index = 0;
            while (index < cssSelector.length()) {
                char ch = cssSelector.charAt(index);
                boolean doSplit;
                if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')) {
                    doSplit = false;
                } else if (ch == '"' || ch == '\'') {
                    int end = cssSelector.indexOf(ch, index + 1);
                    if (end >= 0) {
                        String buffStr = buff.toString();
                        if (!buffStr.isEmpty()) {
                            words.add(buffStr);
                        }
                        buff = new StringBuilder();
                        String str = cssSelector.substring(index + 1, end);
                        words.add(str);
                        index = end + 1;
                        continue;
                    } else {
                        break;
                    }
                } else if (ch == '(') {
                    int end = cssSelector.indexOf(')', index + 1);
                    if (end >= 0) {
                        buff.append(cssSelector.substring(index, end + 1));
                        index = end + 1;
                        continue;
                    } else {
                        break;
                    }
                } else {
                    doSplit = false;
                    for (int i = 0; i < splitCh.length; i++) {
                        if (splitCh[i] == ch) {
                            doSplit = true;
                            break;
                        }
                    }
                }
                if (doSplit) {
                    String buffStr = buff.toString();
                    if (!buffStr.isEmpty()) {
                        words.add(buffStr);
                    }
                    buff = new StringBuilder();
                    if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r') {
                        words.add(Character.toString(ch));
                    }
                } else {
                    buff.append(ch);
                }
                index++;
            }
            String buffStr = buff.toString();
            if (!buffStr.isEmpty()) {
                words.add(buffStr);
            }
            return words;
        }

        /**
         * 将一个选择器应用于上下文元素查找
         *
         * @param context 上下文元素组
         * @param oper1 操作字符1(0,' ','.','#',':','>','+','~','[')
         * @param str1 字符串1 对应操作符1使用
         * @return 查找到的结果
         */
        private static ArrayList<NodeElement> searchElement(ArrayList<Node> context, char oper1, String str1) {
            return searchElement(context, oper1, str1, ' ', null);
        }

        /**
         * 将一个选择器应用于上下文元素查找
         *
         * @param context 上下文元素组
         * @param oper1 操作字符1(0,' ','.','#',':','>','+','~','[')
         * @param str1 字符串1 对应操作符1使用
         * @param oper2 操作字符2 属性操作(0,' ','.','#',':','>','+','~','[')
         * @param str2 字符串2 用于属性操作
         * @return 查找到的结果
         */
        private static ArrayList<NodeElement> searchElement(ArrayList<Node> context, char oper1, String str1, char oper2, String str2) {
            ArrayList<NodeElement> result = new ArrayList<NodeElement>();
            ArrayList<NodeElement> findNodes = null;
            switch (oper1) {
                case 0:
                case ' ':
                    for (Node cNode : context) {
                        findNodes = cNode.getElementsByTagName(str1);
                    }
                    break;
                case '#':
                    for (Node cNode : context) {
                        findNodes = cNode.getElementById(str1);
                    }
                    break;
                case '.':
                    for (Node cNode : context) {
                        findNodes = cNode.getElementsByClassName(str1);
                    }
                    break;
                case '>':
                    findNodes = filterChildElementsByTagName(context, str1);
                    break;
                case '+':
                    findNodes = filterNextElementsByTagName(context, str1, true);
                    break;
                case '~':
                    findNodes = filterNextElementsByTagName(context, str1, false);
                    break;
                case ':':
                    findNodes = filterElementsByPseudoSelector(context, str1);
                    break;
                case '[':
                    findNodes = filterElementsByAttr(context, str1, str2, oper2);
                    break;
                default:
                    break;
            }
            if (findNodes != null) {
                for (NodeElement fNode : findNodes) {
                    if (!result.contains(fNode)) {
                        result.add(fNode);
                    }
                }
            }
            return result;
        }

        /**
         * 通过属性过滤当前上下文节点
         *
         * @param context 上下文节点组
         * @param attrName 属性名称
         * @param attrValue 属性值
         * @param oper 操作符
         * @return 过滤后的结果节点
         */
        private static ArrayList<NodeElement> filterElementsByAttr(ArrayList<Node> context, String attrName, String attrValue, char oper) {
            ArrayList<NodeElement> result = new ArrayList<NodeElement>();
            for (Node node : context) {
                if (node instanceof NodeElement) {
                    NodeElement element = (NodeElement) node;
                    if (attrValue == null) {//need attribute exist
                        if (element.getAttr(attrName) != null) {
                            result.add(element);
                        }
                    } else {
                        String getValue = element.getAttrValue(attrName);
                        if (getValue != null) {
                            switch (oper) {
                                case 0:
                                case ' ':
                                    if (attrValue.equals(getValue)) {
                                        result.add(element);
                                    }
                                    break;
                                case '~':
                                    String[] getValues = getValue.split("\\s");
                                    for (String val : getValues) {
                                        if (attrValue.equals(val)) {
                                            result.add(element);
                                            break;
                                        }
                                    }
                                    break;
                                case '^':
                                    if (getValue.startsWith(attrValue)) {
                                        result.add(element);
                                    }
                                    break;
                                case '$':
                                    if (getValue.endsWith(attrValue)) {
                                        result.add(element);
                                    }
                                    break;
                                case '*':
                                    if (getValue.indexOf(attrValue) >= 0) {
                                        result.add(element);
                                    }
                                    break;
                                case '|':
                                    String[] values1 = getValue.split("-");
                                    String[] values2 = attrValue.split("-");
                                    boolean match = true;
                                    for (int i = 0; i < values2.length; i++) {
                                        if (!values2[i].equals(values1[i])) {
                                            match = false;
                                            break;
                                        }
                                    }
                                    if (match) {
                                        result.add(element);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
            return result;
        }

        /**
         * 通过标签名过滤子节点
         *
         * @param context 上下文节点组
         * @param tagName 标签名
         * @return 过滤后的结果节点
         */
        private static ArrayList<NodeElement> filterChildElementsByTagName(ArrayList<Node> context, String tagName) {
            ArrayList<NodeElement> result = new ArrayList<NodeElement>();
            boolean addAll = "*".equals(tagName);
            for (Node node : context) {
                if (node.children != null) {
                    for (Node child : node.children) {
                        if (child instanceof NodeElement) {
                            NodeElement element = (NodeElement) child;
                            if (addAll || tagName.equals(element.name)) {
                                result.add(element);
                            }
                        }
                    }
                }
            }
            return result;
        }

        /**
         * 通过伪选择器过滤元素
         *
         * @param context 上下文节点组
         * @param selector 选择器
         * @return 过滤后的结果节点
         */
        private static ArrayList<NodeElement> filterElementsByPseudoSelector(ArrayList<Node> context, String selector) {
            ArrayList<NodeElement> result = new ArrayList<NodeElement>();
            selector = selector.toLowerCase();
            String inner = null;
            int startIndex = selector.indexOf('(');
            if (startIndex >= 0) {
                int endIndex = selector.indexOf(')');
                inner = selector.substring(startIndex + 1, endIndex);
                selector = selector.substring(0, startIndex);
            }
            for (Node node : context) {
                if ("root".equals(selector)) {
                    if (node instanceof NodeElement) {
                        NodeElement element = (NodeElement) node;
                        while (element.parent != null && element.parent instanceof NodeElement) {
                            element = (NodeElement) element.parent;
                        }
                        result.add(element);
                    }
                } else if ("nth-child".equals(selector)) {
                    try {
                        int index = Integer.parseInt(inner);
                        if (node instanceof NodeElement && node.parent != null) {
                            NodeElement element = (NodeElement) node;
                            ArrayList<NodeElement> children = node.parent.getChildElements();
                            if (index >= 0 && index < children.size()) {
                                if (children.get(index).equals(element)) {
                                    result.add(element);
                                }
                            }
                        }
                    } catch (Exception ex) {
                    }
                } else if ("nth-last-child".equals(selector)) {
                    try {
                        int index = Integer.parseInt(inner);
                        if (node instanceof NodeElement && node.parent != null) {
                            NodeElement element = (NodeElement) node;
                            ArrayList<NodeElement> children = node.parent.getChildElements();
                            int size = children.size();
                            if (index >= 0 && index < size) {
                                if (children.get(size - index - 1).equals(element)) {
                                    result.add(element);
                                }
                            }
                        }
                    } catch (Exception ex) {
                    }
                } else if ("nth-of-type".equals(selector)) {
                    try {
                        int index = Integer.parseInt(inner);
                        if (node instanceof NodeElement && node.parent != null) {
                            NodeElement element = (NodeElement) node;
                            ArrayList<NodeElement> children = node.parent.getChildElements(element.name);
                            if (index >= 0 && index < children.size()) {
                                if (children.get(index).equals(element)) {
                                    result.add(element);
                                }
                            }
                        }
                    } catch (Exception ex) {
                    }
                } else if ("nth-last-of-type".equals(selector)) {
                    try {
                        int index = Integer.parseInt(inner);
                        if (node instanceof NodeElement && node.parent != null) {
                            NodeElement element = (NodeElement) node;
                            ArrayList<NodeElement> children = node.parent.getChildElements(element.name);
                            int size = children.size();
                            if (index >= 0 && index < size) {
                                if (children.get(size - index - 1).equals(element)) {
                                    result.add(element);
                                }
                            }
                        }
                    } catch (Exception ex) {
                    }
                } else if ("first-child".equals(selector)) {
                    if (node instanceof NodeElement && node.parent != null) {
                        NodeElement element = (NodeElement) node;
                        ArrayList<NodeElement> children = node.parent.getChildElements();
                        if (!children.isEmpty()) {
                            if (children.get(0).equals(element)) {
                                result.add(element);
                            }
                        }
                    }
                } else if ("last-child".equals(selector)) {
                    if (node instanceof NodeElement && node.parent != null) {
                        NodeElement element = (NodeElement) node;
                        ArrayList<NodeElement> children = node.parent.getChildElements();
                        if (!children.isEmpty()) {
                            if (children.get(children.size() - 1).equals(element)) {
                                result.add(element);
                            }
                        }
                    }
                } else if ("first-of-type".equals(selector)) {
                    if (node instanceof NodeElement && node.parent != null) {
                        NodeElement element = (NodeElement) node;
                        ArrayList<NodeElement> children = node.parent.getChildElements(element.name);
                        if (!children.isEmpty()) {
                            if (children.get(0).equals(element)) {
                                result.add(element);
                            }
                        }
                    }
                } else if ("last-of-type".equals(selector)) {
                    if (node instanceof NodeElement && node.parent != null) {
                        NodeElement element = (NodeElement) node;
                        ArrayList<NodeElement> children = node.parent.getChildElements(element.name);
                        if (!children.isEmpty()) {
                            if (children.get(children.size() - 1).equals(element)) {
                                result.add(element);
                            }
                        }
                    }
                } else if ("only-child".equals(selector)) {
                    if (node instanceof NodeElement && node.parent != null) {
                        NodeElement element = (NodeElement) node;
                        ArrayList<NodeElement> children = node.parent.getChildElements();
                        if (children.size() == 1) {
                            result.add(element);
                        }
                    }
                } else if ("only-of-type".equals(selector)) {
                    if (node instanceof NodeElement && node.parent != null) {
                        NodeElement element = (NodeElement) node;
                        ArrayList<NodeElement> children = node.parent.getChildElements(element.name);
                        if (children.size() == 1) {
                            result.add(element);
                        }
                    }
                } else if ("empty".equals(selector)) {
                    if (node instanceof NodeElement) {
                        NodeElement element = (NodeElement) node;
                        if (element.children == null || element.children.isEmpty()) {
                            result.add(element);
                        }
                    }
                }
            }
            return result;
        }

        /**
         * 过滤后续节点
         *
         * @param context 上下文节点组
         * @param tagName 标签名
         * @return 过滤后的结果节点
         */
        private static ArrayList<NodeElement> filterNextElementsByTagName(ArrayList<Node> context, String tagName, boolean immediately) {
            boolean addAll = "*".equals(tagName);
            ArrayList<NodeElement> result = new ArrayList<NodeElement>();
            for (Node node : context) {
                if (node.parent != null) {
                    boolean getNode = false;
                    for (int i = 0; i < node.parent.children.size(); i++) {
                        if (getNode) {
                            Node child = node.parent.children.get(i);
                            if (child instanceof NodeElement) {
                                NodeElement element = (NodeElement) child;
                                if (addAll || tagName.equals(element.name)) {
                                    result.add(element);
                                    break;
                                }
                                if (immediately) {
                                    break;
                                }
                            }
                        }
                        if (!getNode && node.parent.children.get(i).equals(node)) {
                            getNode = true;
                        }
                    }

                }
            }
            return result;
        }

        /**
         * 获取当前节点父节点层数
         *
         * @return 当前节点父节点层数
         */
        protected int getLevel() {
            int level = 0;
            Node node = this.parent;
            while (node != null) {
                level++;
                node = node.parent;
            }
            return level;
        }
    }

    /**
     * 文本节点
     */
    public static class NodeText extends Node {

        /**
         * 文本
         */
        public String text;

        /**
         * 文本节点构造函数
         *
         * @param text 文本
         */
        public NodeText(String text) {
            this.text = text;
        }

        @Override
        public String toString(boolean format, String space, String line, int levelref) {
            return text;
        }
    }

    /**
     * 注释节点，!--标记的声明内容
     */
    public static class NodeComment extends Node {

        /**
         * 注释
         */
        public String comment;

        /**
         * 注释节点构造函数
         *
         * @param comment 注释
         */
        public NodeComment(String comment) {
            this.comment = comment;
        }

        @Override
        public String toString(boolean format, String space, String line, int levelref) {
            return "<!--" + comment + "-->";
        }
    }

    /**
     * 声明节点，?标记的声明内容
     */
    public static class NodeDec extends Node {

        /**
         * 声明节点名称
         */
        public String name;
        /**
         * 声明节点内容
         */
        public String content;

        /**
         * 元素节点构造函数
         *
         */
        public NodeDec() {
            this.name = null;
            this.content = null;
        }

        /**
         * 元素节点构造函数
         *
         * @param name 节点名称
         */
        public NodeDec(String name) {
            this.name = name;
            this.content = null;
        }

        /**
         * 元素节点构造函数
         *
         * @param name 节点名称
         * @param content 节点内容
         */
        public NodeDec(String name, String content) {
            this.name = name;
            this.content = content;
        }

        @Override
        public String toString(boolean format, String space, String line, int levelref) {
            StringBuilder sb = new StringBuilder("<?");
            if (name != null) {
                sb.append(name);
            }
            if (content != null) {
                sb.append(content);
            }
            return sb.append("?>").toString();
        }
    }

    /**
     * 文档类型定义节点，!标记的DTD内容
     */
    public static class NodeDef extends Node {

        /**
         * 元素名称
         */
        public String name;
        /**
         * 元素内容
         */
        public String content;

        /**
         * 文档类型定义节点构造函数
         *
         * @param name 元素名称
         */
        public NodeDef(String name) {
            this.name = name;
            this.content = null;
        }

        /**
         * 文档类型定义节点构造函数
         *
         * @param name 元素名称
         * @param content 元素内容
         */
        public NodeDef(String name, String content) {
            this.name = name;
            this.content = content;
        }

        @Override
        public String toString(boolean format, String space, String line, int levelref) {
            StringBuilder sb = new StringBuilder("<!");
            sb.append(name);
            if (content != null) {
                sb.append(content);
            }
            return sb.append(">").toString();
        }
    }

    /**
     * 元素节点
     */
    public static class NodeElement extends Node {

        /**
         * 元素节点名称
         */
        public String name;
        /**
         * 元素节点属性
         */
        public ArrayList<Attribute> attrs;
        /**
         * 是否为闭合标签（无内容）
         */
        public boolean closed;

        /**
         * 元素节点构造函数
         *
         * @param name 节点名称
         */
        public NodeElement(String name) {
            this.name = name;
            this.attrs = null;
            this.closed = false;
        }

        /**
         * 元素节点构造函数
         *
         * @param name 节点名称
         * @param closed 是否为闭合标签
         */
        public NodeElement(String name, boolean closed) {
            this.name = name;
            this.attrs = null;
            this.closed = closed;
        }

        /**
         * 元素节点构造函数
         *
         * @param name 节点名称
         * @param attrs 节点属性
         */
        public NodeElement(String name, ArrayList<Attribute> attrs) {
            this.name = name;
            this.attrs = attrs;
            this.closed = false;
        }

        /**
         * 元素节点构造函数
         *
         * @param name 节点名称
         * @param attrs 节点属性
         * @param closed 是否为闭合标签
         */
        public NodeElement(String name, ArrayList<Attribute> attrs, boolean closed) {
            this.name = name;
            this.attrs = attrs;
            this.closed = closed;
        }

        /**
         * 设置属性
         *
         * @param name 属性名称
         * @param value 属性值
         * @return 属性对象
         */
        public boolean setAttr(String name, String value) {
            return setAttr(name, value, true);
        }

        /**
         * 设置属性
         *
         * @param name 属性名称
         * @param value 属性值
         * @param ignoreCase 是否忽略大小写
         * @return 是否新增了属性
         */
        public boolean setAttr(String name, String value, boolean ignoreCase) {
            if (attrs == null) {
                attrs = new ArrayList<Attribute>();
            }
            Attribute attr = getAttr(name, ignoreCase);
            if (attr != null) {
                attr.value = value;
                return false;
            } else {
                attrs.add(new Attribute(name, value));
                return true;
            }
        }

        /**
         * 获取属性
         *
         * @param name 属性名称
         * @return 属性对象
         */
        public Attribute getAttr(String name) {
            return getAttr(name, true);
        }

        /**
         * 获取属性
         *
         * @param name 属性名称
         * @param ignoreCase 是否忽略大小写
         * @return 属性对象
         */
        public Attribute getAttr(String name, boolean ignoreCase) {
            if (attrs != null) {
                for (Attribute attr : attrs) {
                    if (strEqual(attr.name, name, ignoreCase)) {
                        return attr;
                    }
                }
            }
            return null;
        }

        /**
         * 获取属性值
         *
         * @param name 属性名称
         * @return 属性值
         */
        public String getAttrValue(String name) {
            return getAttrValue(name, true);
        }

        /**
         * 获取属性值
         *
         * @param name 属性名称
         * @param ignoreCase 是否忽略大小写
         * @return 属性值
         */
        public String getAttrValue(String name, boolean ignoreCase) {
            Attribute attr = getAttr(name, true);
            return attr == null ? null : attr.value;
        }

        @Override
        public String toString(boolean format, String space, String line, int levelref) {
            StringBuilder sb = new StringBuilder("<");
            sb.append(name);
            if (attrs != null) {
                for (Attribute attr : attrs) {
                    sb.append(" ").append(attr.toString());
                }
            }
            if (closed) {
                return sb.append("/>").toString();
            } else {
                sb.append(">");
                boolean addPrefixSuffix = format && children != null && children.size() > 0;
                if (addPrefixSuffix) {
                    sb.append(line);
                }
                sb.append(super.toString(format, space, line, levelref));
                if (addPrefixSuffix) {
                    sb.append(line);
                    int level = getLevel() + levelref;
                    for (int i = 0; i < level; i++) {
                        sb.append(space);//打印缩进
                    }
                }
                return sb.append("</").append(name).append(">").toString();
            }
        }
    }

    /**
     * 特殊节点，如script和style
     */
    public static class NodeSpecial extends NodeElement {

        /**
         * 节点内容
         */
        public String content;

        /**
         * 特殊节点构造函数
         *
         * @param name 节点名称
         * @param attrs 节点属性
         * @param content 节点内容
         */
        public NodeSpecial(String name, ArrayList<Attribute> attrs, String content) {
            super(name, attrs);
            this.content = content;
            this.closed = content == null;
        }

        @Override
        public String toString(boolean format, String space, String line, int levelref) {
            StringBuilder sb = new StringBuilder("<");
            sb.append(name);
            if (attrs != null) {
                for (Attribute attr : attrs) {
                    sb.append(" ").append(attr.toString());
                }
            }
            if (closed) {
                return sb.append("/>").toString();
            } else {
                sb.append(">");
                if (content != null) {
                    sb.append(content);
                }
                return sb.append("</").append(name).append(">").toString();
            }
        }
    }

    /**
     * 节点属性
     */
    public static class Attribute {

        /**
         * 属性名称
         */
        public String name;
        /**
         * 属性值
         */
        public String value;
        /**
         * 引用字符，无引用字符则为0
         */
        public char quote;
        /**
         * 无引用字符使用的字符值
         */
        public static final char NONE_QUOTE = 0;
        /**
         * 默认的引用字符
         */
        public static final char DEFAULT_QUOTE = '"';

        /**
         * 节点属性构造函数
         *
         * @param name 属性名称
         * @param value 属性值
         */
        public Attribute(String name, String value) {
            this.name = name;
            this.value = value;
            this.quote = DEFAULT_QUOTE;

        }

        /**
         * 节点属性构造函数
         *
         * @param name 属性名称
         * @param value 属性值
         * @param quote 引用字符，无引用字符则为0
         */
        public Attribute(String name, String value, char quote) {
            this.name = name;
            this.value = value;
            this.quote = quote;
        }

        @Override
        public String toString() {
            if (quote == NONE_QUOTE) {
                if (value == null) {
                    return name;
                } else {
                    return name + "=" + value;
                }
            } else {
                if (value == null) {
                    return name;
                } else {
                    return name + "=" + quote + value + quote;
                }
            }
        }
    }
    /**
     * 文档根节点
     */
    public Node document;

    /**
     * 文档构造函数
     */
    private DomParser(Node document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return document.toString();
    }

    /**
     * 创建元素节点
     *
     * @param name 节点标签名
     * @return 元素节点对象
     */
    public static NodeElement createElement(String name) {
        return new NodeElement(name);
    }

    /**
     * 创建空文档
     *
     * @return 空文档对象
     */
    public static DomParser create() {
        return new DomParser(new Node());
    }

    /**
     * 创建构造的文档
     *
     * @param document 文档根节点
     * @return 文档对象
     */
    public static DomParser create(Node document) {
        return new DomParser(document);
    }

    /**
     * 创建转换字符串为DOM文档
     *
     * @param document 文档字符串
     * @return 文档对象
     */
    public static DomParser create(String document) {
        return new DomParser(build(document, false));
    }

    /**
     * 创建转换字符串为DOM文档
     *
     * @param document 文档字符串
     * @param xmlmode 是否以XML解析，区分大小写，不去除特殊标签（script/style）
     * @return 文档对象
     */
    public static DomParser create(String document, boolean xmlmode) {
        return new DomParser(build(document, xmlmode));
    }

    /**
     * 从文件读取DOM文档
     *
     * @param file 文件
     * @return 文档对象
     */
    public static DomParser create(File file) throws IOException {
        return create(file, null);
    }

    /**
     * 从文件读取DOM文档
     *
     * @param file 文件
     * @param encode 文件编码
     * @return 文档对象
     */
    public static DomParser create(File file, String encode) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader;
        if (encode == null) {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } else {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
        }
        try {
            int read;
            char[] buff = new char[1024];
            while ((read = reader.read(buff)) != -1) {
                sb.append(buff, 0, read);
            }
        } finally {
            reader.close();
        }
        return create(sb.toString());
    }

    /**
     * 转换文档字符串为文档节点
     *
     * @param document 文档字符串
     * @return 文档节点
     */
    private static Node build(String document, boolean xmlmode) {
        Node root = new Node();
        build(root, document, 0, document.length(), xmlmode);
        return root;
    }

    /**
     * 构造节点
     *
     * @param context 上下文节点
     * @param source 内容源
     * @param start 起始索引
     * @param end 截止索引
     */
    private static void build(Node context, String source, int start, int end, boolean xmlmode) {
        int index = start;
        while (index < end) {
            //查找起始标签
            int tagStartIndex = source.indexOf('<', index);
            //处理中间文本
            if (tagStartIndex < 0 || tagStartIndex >= end) {//搜索结束，添加最后的文本节点
                String text = source.substring(index, end);
                context.addChild(new NodeText(text));
                break;//读取到结束
            } else if (tagStartIndex > index) {//发现标签，添加前导文本
                String text = source.substring(index, tagStartIndex);
                context.addChild(new NodeText(text));
            }
            //处理标签
            if (tagStartIndex + 1 >= end) {
                break;//读取到结束
            }
            char firstChar = source.charAt(tagStartIndex + 1);
            if (firstChar == '?') {
                int nameEnd = searchWordEnd(source, tagStartIndex + 2, end);
                if (nameEnd < 0 || nameEnd >= end) {
                    break;
                }
                String name = source.substring(tagStartIndex + 2, nameEnd);
                int tagEndIndex = searchMatchStr(source, nameEnd, end, "?>", !xmlmode, true);
                if (tagEndIndex < 0 || tagEndIndex >= end) {
                    break;
                }
                String content = source.substring(nameEnd, tagEndIndex);
                context.addChild(new NodeDec(name, content));
                index = tagEndIndex + 2;
                continue;
            } else if (firstChar == '!') {
                if (tagStartIndex + 3 < end
                        && source.charAt(tagStartIndex + 2) == '-'
                        && source.charAt(tagStartIndex + 3) == '-') {
                    //处理注释内容
                    int commentEndIndex = source.indexOf("-->", tagStartIndex + 4);
                    if (commentEndIndex < 0 || commentEndIndex >= end) {
                        break;
                    } else {
                        String comment = source.substring(tagStartIndex + 4, commentEndIndex);
                        context.addChild(new NodeComment(comment));
                        index = commentEndIndex + 3;
                        continue;//get comment,continue
                    }
                } else {
                    int nameEnd = searchWordEnd(source, tagStartIndex + 2, end);
                    if (nameEnd < 0 || nameEnd >= end) {
                        break;
                    }
                    String name = source.substring(tagStartIndex + 2, nameEnd);
                    int tagEndIndex = searchMatchChar(source, nameEnd, end, '>', !xmlmode, true);
                    if (tagEndIndex < 0 || tagEndIndex >= end) {
                        break;
                    }
                    String content = source.substring(nameEnd, tagEndIndex);
                    context.addChild(new NodeDef(name, content));
                    index = tagEndIndex + 1;
                    continue;
                }
            } else if (firstChar == '/') {
                int tagEndIndex = searchMatchChar(source, tagStartIndex + 2, end, '>', !xmlmode, false);
                String name = source.substring(tagStartIndex + 2, tagEndIndex).trim();
                if (context instanceof NodeElement) {
                    NodeElement elmNode = (NodeElement) context;
                    if (!elmNode.closed && strEqual(name, elmNode.name, !xmlmode)) {
                        if (context.parent != null) {
                            context = context.parent;
                        }
                    }
                }
                index = tagEndIndex + 1;
                continue;
            } else {
                int nameEnd = searchWordEnd(source, tagStartIndex + 1, end);
                if (nameEnd < 0 || nameEnd >= end) {
                    break;
                }
                String name = source.substring(tagStartIndex + 1, nameEnd);
                int tagEndIndex = searchMatchChar(source, nameEnd, end, '>', !xmlmode, false);
                int closeSymbolIndex = searchMatchChar(source, nameEnd, tagEndIndex, '/', !xmlmode, false);
                boolean selfClose = closeSymbolIndex >= 0 && closeSymbolIndex < tagEndIndex;
                closeSymbolIndex = closeSymbolIndex < 0 ? tagEndIndex : closeSymbolIndex;
                if (singleTag(name, xmlmode) >= 0) {
                    selfClose = true;
                }
                ArrayList<Attribute> attrs = buildAttrs(source, nameEnd, selfClose ? closeSymbolIndex : tagEndIndex);
                int specialIndex = specialTag(name, xmlmode);
                if (specialIndex >= 0) {
                    if (!selfClose) {
                        int specialSearchIndex = tagEndIndex + 1;
                        int contentStartIndex = specialSearchIndex;
                        int contentEndIndex = end;
                        boolean find = false;
                        while (specialSearchIndex < end && !find) {
                            contentEndIndex = searchMatchStr(source, specialSearchIndex, end, "</", !xmlmode, false);
                            if (contentEndIndex < 0 || contentEndIndex >= end) {
                                break;
                            }
                            int closeTagEndIndex = searchMatchChar(source, contentEndIndex + 2, end, '>', !xmlmode, false);
                            if (closeTagEndIndex < 0 || closeTagEndIndex >= end) {
                                break;
                            }
                            String closeTagName = source.substring(contentEndIndex + 2, closeTagEndIndex);
                            if (strEqual(name, closeTagName.trim(), !xmlmode)) {
                                find = true;
                            }
                            specialSearchIndex = closeTagEndIndex + 1;
                        }
                        if (find) {
                            String content = source.substring(contentStartIndex, contentEndIndex);
                            NodeSpecial speNode = new NodeSpecial(name, attrs, content);
                            context.addChild(speNode);
                            index = specialSearchIndex;
                            continue;
                        } else {
                            break;
                        }
                    } else {
                        NodeSpecial speNode = new NodeSpecial(name, attrs, null);
                        context.addChild(speNode);
                        index = tagEndIndex + 1;
                        continue;
                    }
                } else {
                    NodeElement elmNode = new NodeElement(name, attrs, selfClose);
                    context.addChild(elmNode);
                    if (!selfClose) {
                        context = elmNode;
                    }
                    index = tagEndIndex + 1;
                }
                continue;
            }
        }
    }

    /**
     * 是否为特殊标签，内部不会被解析
     *
     * @param name 标签名称
     * @param xmlmode 是否xml模式（无特殊标签）
     * @return 特殊标签数组索引，不匹配返回-1
     */
    private static int specialTag(String name, boolean xmlmode) {
        if (xmlmode) {
            return -1;
        } else {
            name = name.toLowerCase();
            for (int i = 0; i < SPECIAL_TAG.length; i++) {
                if (SPECIAL_TAG[i].equals(name)) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * 是否为特殊标签，无内容的单标签
     *
     * @param name 标签名称
     * @param xmlmode 是否xml模式（无特殊标签）
     * @return 特殊标签数组索引，不匹配返回-1
     */
    private static int singleTag(String name, boolean xmlmode) {
        if (xmlmode) {
            return -1;
        } else {
            name = name.toLowerCase();
            for (int i = 0; i < SINGLE_TAG.length; i++) {
                if (SINGLE_TAG[i].equals(name)) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * 获取标签属性
     *
     * @param source html代码
     * @param start 标签属性起始索引
     * @param end 标签属性截止索引
     * @return 标签属性
     */
    private static ArrayList<Attribute> buildAttrs(String source, int start, int end) {
        ArrayList<Attribute> attrs = new ArrayList<Attribute>();
        int index = start;
        while (index < end) {
            //Step-1:get attr name
            int indexStart = searchNextChar(source, index, end);
            if (indexStart < 0) {
                break;//html end
            }
            int indexEnd = searchWordEnd(source, indexStart, end);
            if (indexEnd >= end) {
                break;//html end
            }
            String attrName = source.substring(indexStart, indexEnd);
            //Step-2:get '='
            indexStart = searchNextChar(source, indexEnd, end);
            if (indexStart < 0) {
                attrs.add(new Attribute(attrName, null));
                break;//html end
            }
            char ch = source.charAt(indexStart);
            if (ch != '=') {
                attrs.add(new Attribute(attrName, null));
                index = indexStart;
                continue;//new attr start
            }
            //Step-3:get attr value
            indexStart = searchNextChar(source, indexStart + 1, end);
            if (indexStart < 0) {
                attrs.add(new Attribute(attrName, null));
                break;//html end
            }
            ch = source.charAt(indexStart);
            String attrValue;
            if (ch == '\'' || ch == '"') {//quote value
                indexStart = indexStart + 1;
                indexEnd = source.indexOf(ch, indexStart);
                if (indexEnd >= 0 && indexEnd < end) {
                    attrValue = source.substring(indexStart, indexEnd);
                    attrs.add(new Attribute(attrName, attrValue, ch));
                    index = indexEnd + 1;
                } else {
                    attrValue = source.substring(indexStart, end);
                    attrs.add(new Attribute(attrName, attrValue, ch));
                    break;//html end
                }
            } else {//none quote value
                for (indexEnd = indexStart; indexEnd < end; indexEnd++) {
                    ch = source.charAt(indexEnd);
                    if (ch == ' ' || ch == '>') {
                        break;
                    }
                }
                if (indexEnd < end) {
                    attrValue = source.substring(indexStart, indexEnd);
                    attrs.add(new Attribute(attrName, attrValue, Attribute.NONE_QUOTE));
                    index = indexEnd;
                } else {
                    attrValue = source.substring(indexStart, end);
                    attrs.add(new Attribute(attrName, attrValue, Attribute.NONE_QUOTE));
                    break;//html end
                }
            }
        }
        return attrs;
    }

    /**
     * 查找下一个非空字符索引
     *
     * @param source 字符串源
     * @param start 开始索引
     * @param end 截止索引
     * @return 下一个非空字符索引，没有找到返回-1
     */
    private static int searchNextChar(String source, int start, int end) {
        if (start >= 0) {
            for (int i = start; i < source.length() && i < end; i++) {
                char ch = source.charAt(i);
                if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r') {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 查找单词结尾索引
     *
     * @param source 字符串源
     * @param start 开始索引
     * @param end 截止索引
     * @return 单词结尾索引，或截止索引
     */
    private static int searchWordEnd(String source, int start, int end) {
        if (start >= 0) {
            for (int i = start; i < source.length() && i < end; i++) {
                if (!isWordChar(source.charAt(i))) {
                    return i;
                }
            }
        }
        return end;
    }

    /**
     * 查找匹配字符串，会匹配多层括号，并忽略字符串内容
     *
     * @param source 字符串
     * @param start 起始索引
     * @param end 截止索引
     * @param searchStr 要查找的字符
     * @param ignoreCase 是否忽略大小写
     * @param hasStack 是否有括号匹配堆栈
     * @return 查找到的匹配右尖括号索引
     */
    private static int searchMatchStr(String source, int start, int end, String searchStr, boolean ignoreCase, boolean hasStack) {
        if (source == null || "".equals(source)) {
            return -1;
        }
        int newEndIndex = end - searchStr.length() + 1;
        char[] chs = searchStr.toCharArray();
        int index = searchMatchChar(source, start, newEndIndex, chs[0], ignoreCase, hasStack);
        while (index >= 0) {
            boolean match = true;
            for (int i = 1; i < chs.length; i++) {
                if (!chEqual(chs[i], source.charAt(index + i), ignoreCase)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return index;
            }
            index = searchMatchChar(source, index + 1, newEndIndex, chs[0], ignoreCase, hasStack);
        }
        return index;
    }

    /**
     * 查找匹配字符，会匹配多层括号，并忽略字符串内容
     *
     * @param source 字符串
     * @param start 起始索引
     * @param end 截止索引
     * @param searchCh 要查找的字符
     * @param ignoreCase 是否忽略大小写
     * @param hasStack 是否有括号匹配堆栈
     * @return 查找到的匹配右尖括号索引
     */
    private static int searchMatchChar(String source, int start, int end, char searchCh, boolean ignoreCase, boolean hasStack) {
        ArrayList<Character> stack = null;
        if (hasStack) {
            stack = new ArrayList<Character>();
        }
        int index = start;
        while (index < end) {
            char ch = source.charAt(index);
            if (chEqual(searchCh, ch, ignoreCase)) {
                if (hasStack) {
                    if (stack.isEmpty()) {
                        return index;
                    }
                } else {
                    return index;
                }
            }
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')
                    || ch == '=' || ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'
                    || ch == '_' || ch == '-' || ch == ':' || ch == '.') {
                index++;
                continue;//skip char to speed up
            }
            if (ch == '"' || ch == '\'') {//solve string
                int match = source.indexOf(ch, index + 1);
                if (match >= 0 && match < end) {
                    index = match + 1;
                    continue;//get string end continus
                } else {
                    break;//until end not find string end quote
                }
            }
            if (hasStack) {
                if (ch == '<' || ch == '(' || ch == '[' || ch == '{') {
                    stack.add(ch);
                    index++;
                    continue;//push left symbol
                }
                if (ch == '>' || ch == ')' || ch == ']' || ch == '}') {
                    int size = stack.size();
                    if (stack.isEmpty()) {
                        break;
                    }
                    char stackCh = stack.get(size - 1).charValue();
                    boolean match = false;
                    if (!match && stackCh == '<' && ch == '>') {
                        match = true;
                    }
                    if (!match && stackCh == '(' && ch == ')') {
                        match = true;
                    }
                    if (!match && stackCh == '[' && ch == ']') {
                        match = true;
                    }
                    if (!match && stackCh == '{' && ch == '}') {
                        match = true;
                    }
                    if (match) {
                        stack.remove(size - 1);
                        index++;
                        continue;//get right symbol
                    } else {
                        break;
                    }
                }
            }
            //other character
            index++;
        }
        return -1;
    }

    /**
     * 判断两个字符是否相等
     *
     * @param ch1 字符1
     * @param ch2 字符2
     * @param ignoreCase 是否忽略大小写
     * @return 是否相等
     */
    private static boolean chEqual(char ch1, char ch2, boolean ignoreCase) {
        if (ignoreCase) {
            return Character.toLowerCase(ch1) == Character.toLowerCase(ch2);
        } else {
            return ch1 == ch2;
        }
    }

    /**
     * 判断两个字符串是否相等
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @param ignoreCase 是否忽略大小写
     * @return 是否相等，null不与任何字符串相等
     */
    private static boolean strEqual(String str1, String str2, boolean ignoreCase) {
        if (str1 == null || str2 == null) {
            return false;
        } else {
            if (ignoreCase) {
                return str1.toLowerCase().equals(str2.toLowerCase());
            } else {
                return str1.equals(str2);
            }
        }
    }

    /**
     * 判断一个字符是否为一个单词字符
     *
     * @param ch 字符
     * @return 是否为一个单词字符
     */
    private static boolean isWordChar(char ch) {
        if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')
                || ch == '_' || ch == '-' || ch == ':' || ch == '.') {
            return true;
        }
        if (ch == ' ' || ch == '=' || ch == '\t' || ch == '\n' || ch == '\r'
                || ch == '<' || ch == '>' || ch == '!' || ch == '>' || ch == '(' || ch == ')'
                || ch == '[' || ch == ']' || ch == '{' || ch == '}') {
            return false;
        }
        return true;
    }
}
