#文本行形式数据除错工具

配合之前写的DatabaseImport使用。用来检查每一行都已经格式化好了的数据文件（这个工具会扫描文件夹下面所有的.txt文件）并输出到目的文件夹


```
java -jar LineDataChecker.jar -s [source dir] -o [output dir] -r [regex] -pr [t/f] [-po output dir]
java -jar LineDataChecker.jar -s [source dir] -o [output dir] -sc [split char] -fc [field count] -pr [t/f] [-po output dir]
java -jar LineDataChecker.jar -s [source dir] -o [output dir] -sc [split char] -fcr [field count range] -pr [t/f] [-po output dir]

-s   | --source             源文件夹
-o   | --output             目标输出文件夹
-r   | --regex              使用Java格式的正则表达式检查（转义字符要写两次）
-sc  | --split-char         使用字段格式检查，设置字段分隔符（可以使用诸如\t \r这种）
-fc  | --field-count        每行的字段数量
-fcr | --field-count-range  字段数量用范围代替
-po  | --problem-output     设置把不符合的行输出到哪里
-pr  | --pre-load           预读取模式，会先把文件读入到内存
-h   | --help               帮助信息
```