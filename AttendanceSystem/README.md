# Attendance-System

## **服务器说明**

### 配置文件（urlAuthority.settings）

- **urlAuthority.settings**位于src\main\resources中
  
#### **格式**

> [网址] [HTTP方法] [AND|OR] ([>|>=|=|<|<=] [权限])+  
> [网址] [HTTP方法] [DYNAMIC]  
> 支持以#或//开头的**注释**（//前需有空格）

#### **说明**

该**配置文件**用于配置各网址和HTTP方法的组合所需的**权限**

- **一行**为一条设置
- 网址需**符合**AntPathRequestMatcher的pattern的格式
- HTTP方法包括**PUT POST GET DELETE**等
- **AND**表示需要满足**所有**权限，**OR**表示只需满足**其中一个**权限
- **DYNAMIC**表示**动态权限判断**，具体判断方式见权限表
- **\>|>=|=|<|<=**：比较权限的方法，根据User类中的对权限的定义来比较
- **可用权限列表**：

    - STAFF = **0**
    - DEPARTMENT_MANAGER = **1**
    - GENERAL_MANAGER = **2**
    - GENERAL = **-1**
    - PERSONNEL = **-2**
    - ADMINISTRATOR = **-4**

### 登录方式

- 目前仅支持**json**登录，不可使用**相同**的**加密后的密码**登录，登录成功后可获取***token***。

### 注销方式

- 目前仅支持**json**注销，不可使用**相同**的**加密后的密码**注销

### 身份验证方式

- 目前支持**json**和**token**来验证身份。**Json**验证身份流程同登录流程。
