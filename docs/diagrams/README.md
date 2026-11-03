# docs/diagrams 说明

本目录放架构与权限图的 PlantUML 源文件和 PNG。PNG 用于 GitHub 预览和 Word 报告，`.puml` 用于修改。

| 文件 | 内容 | 用在哪 |
|---|---|---|
| `architecture-logical.puml` / `.png` | 逻辑架构：客户端/接入/应用/数据四层 + 六个模块 | 设计文档 4.4、实验报告 2.1.2 |
| `architecture-deployment.puml` / `.png` | 部署架构：浏览器/Nginx/Spring Boot/MySQL/volume | 设计文档 5.6、实验报告 2.1.3 |
| `permission-gates.puml` / `.png` | 权限闸门：认证→角色→归属→状态 | 设计文档 7.14、答辩讲解 |

## 怎么渲染

**VS Code（推荐）**

1. 安装扩展 `PlantUML`（jebbs.plantuml）；
2. 打开 `.puml` 文件；
3. `Ctrl+Shift+P` → `PlantUML: Preview Current Diagram`；
4. 右键预览 → `Export Current Diagram` → 选 PNG，覆盖同名文件。

**在线**

<https://www.plantuml.com/plantuml/uml/>，把 `.puml` 内容粘进去。
> 图中只有架构信息，没有患者数据、密钥；不要把 `.env`、真实数据粘到在线网站。

## 修改规则

- 先改 `.puml`，再导出 `.png`，两个一起提交；
- 改完在 PR 描述里贴一张预览截图或说明改了哪几个框；
- 图里的模块名、接口名必须和 `docs/02-design/系统设计规格说明书.md` 一致；
- 不要直接改 PNG（二进制没法 review）。
