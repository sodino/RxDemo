
[TOC]


[TOC]

# git删除敏感文件和数据

### 使用场景
工程刚创建时，把`keystore`文件及`password`等关键文件或数据提交到代码仓库了。  
当然也可以是其它不想公开的敏感数据。  
这时就需要将这些数据从所有的分支中所有的提交记录中抹去。

## 构建敏感数据场景

如下图，在Demo工程中上传了`keystore`文件及明文`password`。

![sensitive.data](https://wx2.sinaimg.cn/mw1024/e3dc9ceagy1fqp8lfn1iej216s0qgtcv.jpg)


## 下载BFG.jar
本文使用[BFG(bfg-repo-cleaner)](https://rtyley.github.io/bfg-repo-cleaner/)配合`git`命令实现以上需求。
先从网官上下载`bfg.jar`。

## 编辑`replace-text`
对于`app.password.txt`中`password`的内容，需要使用其它文本覆盖掉。需要编辑`replace-text`。官方给的替换示例如下（不包含注释内容）
```
PASSWORD1                       # Replace with '***REMOVED***' (default)
PASSWORD2==>examplePass         # replace with 'examplePass' instead
PASSWORD3==>                    # replace with the empty string
regex:password=\w+==>password=  # Replace, using a regex
regex:\r(\n)==>$1               # Replace Windows newlines with Unix newlines
```

所以，对于本次示例，`replace-text`的内容为：
```
test!@!2018
keyAlias      "MainActivity"==>keyAlias      "***REMOVED***"
```
并将以上内容保存`replace.txt`中。

## 执行擦除操作


#### 下载工程镜像
```
cd ~/tmp        // 将工程镜像置于tmp文件夹
git clone --mirror https://github.com/sodino/Demo.git
cd Demo.git   // 命令行进入Demo.git文件夹
```

并将`bfg.jar`及`replace.txt`复制到`Demo.git`文件夹中。

#### 清除敏感数据及文件
```
// 当前在Demo.git文件夹运行如下命令：

java -jar bfg-1.13.0.jar --delete-files app.2018.keystore --replace-text replace.txt .
// 注意，结尾有个`.`
```

如果碰到`Protected commits`的异常提示，

```
Protected commits
-----------------

These are your protected commits, and so their contents will NOT be altered:

 * commit 1363e644 (protected by 'HEAD') - contains 2 dirty files : 
    - app/app.2018.keystore (1.0 KB)
    - app/app.password.txt (84 B)

WARNING: The dirty content above may be removed from other commits, but as
the *protected* commits still use it, it will STILL exist in your repository.
```

有两种处理办法：   

第一种： 请添加上参数`--no-blob-protection`，
即
```
java -jar bfg-1.13.0.jar --delete-files app.2018.keystore --replace-text replace.txt --no-blob-protection .
// 注意，结尾有个`.`
```
> [How to remove a protected commit using BFG](https://stackoverflow.com/questions/43231061/how-to-remove-a-protected-commit-using-bfg)   

第二种：  在`gitlab`上对该分支`upprotect`
在`gitlab`工程首页,`Settings` -> `Repository` -> `Protected branch` -> `Unprotect`。

#### 强行更新代码仓库
```
git reflog expire --expire=now --all && git gc --prune=now --aggressive
```

#### 提交
```
git push
```

在这一步有可能出现：`You are not allowed to force push code to a protected branch on this project.`。
则请执行上文提到的`Unprotect`分支操作。



## 效果验收
如果`app.password.txt`还存在于工程目录中，则`update project`后，需要本处处理`merge`。所以建议先删除`app.password.txt`再进行如上操作。否则先将本地代码文件夹删除再重新拉取一份。

从仓库中新拉取代码，则从历史提交记录中已经找不到`app.2018.keystore`，并且`app.password.txt`中敏感内容也已经被删除。如下图：
![remove.sensitive.data](https://wx4.sinaimg.cn/large/e3dc9ceagy1fqp8xk185wj20hh04cq3k.jpg)






