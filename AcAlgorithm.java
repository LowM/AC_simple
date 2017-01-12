//AcAlgorithm.java
package acTest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ACAlgorithm {
//匹配pattern的个数
public static int K = 4;
public static int sIntCnt = 0;
//记录总的状态数
public static int newstate = 0;
public static HashMap<GotoKey, Integer> GotoTable
= new HashMap<GotoKey, Integer>();
public static HashMap<Integer, LinkedList<String>> output = new HashMap<Integer, LinkedList<String>>();
public static HashMap<Integer, Integer> FailureTable = new HashMap<Integer, Integer>();
public static List<String> strPatternList = new ArrayList<String>();

public static void main(String[] args)
{
String strPattern1 = "测试 没有";
String strPattern4 = "华西 医院";
String strPattern2 = " 三甲 十一 科技 公司 华西 齐鲁大学";
String strPattern3 = "华西医院";
String strPattern5 =" ";
String text = "四川大学华西医院（West China Hospital，Sichuan University）是中国西部疑难危急重症诊"
+ "疗的国家级中心，也是世界规模第一的综合性单点医院，拥有中国规模最大、最早整体通过美国病理家学会（CAP）检查"
+ "认可的医学检验中心。[1] 华西医院起源于美国、加拿大、英国等国基督教会1892年在成都创建的仁济、存仁医院；"
+ "1937年抗日战争全面爆发，中央大学、燕京大学、齐鲁大学、金陵大学、金陵女子文理学院内迁成都，与华西协合大学"
+ "联合办学办医；1938年，有医学院的华大、中大、齐大组建联合医院；1946年，华西协合大学医院在现址全部建成，"
+ "简称华西医院。据2015年12月医院官网信息显示，医院运行院区占地五百余亩，业务用房60余万平方米。设有成都国学"
+ "巷本部及温江医疗院区，全托管成都上锦南府医院（简称“上锦分院”），各院区服务功能定位明确：院本部床位4100张，"
+ "以诊治疑难危重疾病为主；温江院区床位200张，以诊治康复、肿瘤及慢性疾病为主。四川大学华西医院在2015年公布"
+ "的中国公立医院社会贡献度排行榜上总分位列全国第一，在复旦大学中国最佳专科声誉和最佳医院排行榜上，连续五年名列"
+ "全国第二，连续多年被独立第三方调查评选为“全国最受欢迎三甲医院” 前十强、“医疗机构最佳雇主”前十强。[2] ";

GetMatchRst(strPattern1, text);
GetMatchRst(strPattern2, text);
GetMatchRst(strPattern3, text);
GetMatchRst(strPattern4, text);
GetMatchRst(strPattern5, text);
}

public static void printFormatFun(int iRst){
if (iRst >= 1){
System.out.println("------------用例"+ sIntCnt + "匹配成功!-----------------\n");
}
else{
System.out.println("------------用例"+ sIntCnt + "匹配不成功!-----------------\n");
}
}

public static int GetMatchRst(String strPattern, String strText){
//清空ListArr
++sIntCnt;
newstate = 0;
if (!strPatternList.isEmpty()){
strPatternList.clear();
}

if (!GotoTable.isEmpty()){
GotoTable.clear();
}

if (!output.isEmpty()){
output.clear();
}

if (!FailureTable.isEmpty()){
FailureTable.clear();
}

if (strPattern == null || strPattern.length() == 0 || strPattern.trim().isEmpty() ||
strText == null || strText.length() == 0 || strText.trim().isEmpty()){
System.out.println("模式串或者源串为空!");
printFormatFun(-1);
return -1;
}

//构造转向表
GetGotoTable(strPattern);

//构造失败表
GetFailureTable();

//开始匹配
int iRst = GetMatchedStr(strText);
printFormatFun(iRst);
return iRst;
}
//进行匹配
private static int GetMatchedStr(String text) {
int state = 0;
int iSuccCnt = 0;
for(int i = 0; i < text.length(); i++)
{
//当转向表不包含对应字符时按失败表前进
while(i < text.length() && !ContainsKey(GotoTable, new GotoKey(state, text.substring(i, i+1))))
{
//如果失败表也不包含，则按照跳过该字符
if(null == FailureTable.get(state))
{
i++;
continue;
}
state = FailureTable.get(state);
}
//按照转向表转向下一个位置state
if(i < text.length() )
state = GetNext(GotoTable, new GotoKey(state, text.substring(i, i+1)));
if(output.containsKey(state)){
++iSuccCnt;
System.out.print(i + "\t");
System.out.println(output.get(state));
if (iSuccCnt >= 1){
//break;
return i;
}
}//end if out
}//end for
if (0 == iSuccCnt)
{
System.out.println("-1");
return -1;
}
return 1;
}
//构造转向表
private static void GetGotoTable(String strPattern)
{
String[] arrays = strPattern.trim().split(" +");
System.out.println("arrays.size() = " + arrays.length);
for(String strPart : arrays)
{
System.out.println(strPart);
enter(strPart);
//System.out.println(s);
}

//String strPart1 = "测试";
//String strPart2 = "杨";
//enter(strPart1);
//enter(strPart2);

//把所有0—>x都加入到转向表中
Set<GotoKey> S = GotoTable.keySet();
LinkedList<String> otherStr = new LinkedList<String>();
for(GotoKey k : S)
{
if(!ContainsKey(GotoTable, new GotoKey(0, k.ConvertStr)))
{
if(!otherStr.contains(k.ConvertStr))
otherStr.add(k.ConvertStr);
}
}
while(otherStr.size() > 0)
{
GotoTable.put( new GotoKey(0, otherStr.remove(0)), 0);
}
}
//加入新的pattern
private static void enter(String newpattern) {
int state = 0;
int j = 0;

if (!strPatternList.contains(newpattern))
{
strPatternList.add(newpattern);
GotoKey current = new GotoKey(
state, newpattern.substring(j, j+1));
//能走下去，就尽量延用以前的老路子，走不下去，就走下面的for()拓展新路子
while (ContainsKey(GotoTable, current) && j < newpattern.length())
{
//System.out.print("进入");
state = GetNext(GotoTable, current);
j++;
current = new GotoKey(
state, newpattern.substring(j, j+1));
}
//拓展新路子
for (int p = j; p < newpattern.length(); p++)
{
newstate = newstate + 1;
GotoTable.put(new GotoKey(state, newpattern.substring(p, p+1)), newstate);
state = newstate;
}
//此处state为每次构造完一个pat时遇到的那个状态
if(!output.containsKey(state))
output.put(state, new LinkedList<String>());
output.get(state).add(newpattern);
}
else
{
//不做处理，ycy
}

}

//获取转向表由起始state经过转向字符转到的state
private static int GetNext(HashMap<GotoKey, Integer> gotoTable,
GotoKey current) {
Set<GotoKey> S = gotoTable.keySet();
for(GotoKey k : S)
{
if(k.StartState == current.StartState && k.ConvertStr.equals(current.ConvertStr))
return gotoTable.get(k);
}
return -1;
}
//判断转向表是否包含对应转向
private static boolean ContainsKey(HashMap<GotoKey, Integer> gotoTable,GotoKey current) {
Set<GotoKey> S = gotoTable.keySet();
for(GotoKey k : S)
{
if(k.StartState == current.StartState && k.ConvertStr.equals(current.ConvertStr))
return true;
}
return false;
}
//构造failure表
private static void GetFailureTable()
{
//按照广度优先搜索
LinkedList<Integer> queue = new LinkedList<Integer>();
Set<GotoKey> keys = GotoTable.keySet();
//将所有起始state为0且终止state不为0的转向加入队列
for(GotoKey key : keys)
{
if(0 != key.StartState)
{
continue;
}
int s = GetNext(GotoTable, key);
if(s != 0)
{
queue.add(s);
FailureTable.put(s, 0);
}
}
while(!queue.isEmpty())
{
int r = queue.remove(0);
for(GotoKey key : keys)
{
if(key.StartState != r)
{
continue;
}
String a = key.ConvertStr;
int s = GetNext(GotoTable, key);
queue.add(s);
int state = FailureTable.get(r);
//如果转向表不包含对应转向，则按照失败表转向
while(!ContainsKey(GotoTable, new GotoKey(state, a)))
{
state = FailureTable.get(state);
}
FailureTable.put(s, GetNext(GotoTable, new GotoKey(state, a)));
//合并output(f(s))到output(s)
LinkedList<String> outputfs = output.get(FailureTable.get(s));
if(outputfs != null)
output.get(s).addAll(outputfs);
}

}
}
}
