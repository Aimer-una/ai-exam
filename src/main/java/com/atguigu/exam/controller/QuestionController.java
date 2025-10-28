package com.atguigu.exam.controller;

import com.atguigu.exam.common.Result;
import com.atguigu.exam.entity.Question;
import com.atguigu.exam.service.QuestionService;
import com.atguigu.exam.vo.QuestionQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;



/**
 * 题目控制器 - 处理题目相关的HTTP请求
 * 
 * Spring MVC架构教学要点：
 * 1. Controller层：负责接收HTTP请求，调用Service层处理业务逻辑，返回响应
 * 2. RESTful API设计：使用HTTP方法语义（GET查询、POST创建、PUT更新、DELETE删除）
 * 3. 依赖注入：通过@Autowired注解注入Service和Mapper依赖
 * 4. 请求映射：通过@RequestMapping系列注解映射URL路径到处理方法
 * 5. 参数绑定：自动将HTTP请求参数绑定到方法参数
 * 6. 数据返回：统一使用Result包装返回数据，便于前端处理
 * 
 * 业务功能：
 * - 题目的CRUD操作（创建、查询、更新、删除）
 * - 多条件筛选和分页查询
 * - 随机题目获取（用于自动组卷）
 * - 热门题目展示（用于首页推荐）
 * 
 * @author 智能学习平台开发团队
 * @version 1.0
 */
@RestController  // @Controller + @ResponseBody，表示这是一个REST控制器，返回JSON数据
@RequestMapping("/api/questions")  // 设置基础URL路径，所有方法的URL都以此开头
@CrossOrigin(origins = "*")  // 允许跨域访问，解决前后端分离开发中的跨域问题
@Tag(name = "题目管理", description = "题目相关的增删改查操作，包括分页查询、随机获取、热门推荐等功能")  // Swagger标签，用于分组显示API
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    
    /**
     * 分页查询题目列表（支持多条件筛选）
     * 
     * RESTful API教学：
     * - URL：GET /api/questions/list
     * - 查询参数：通过@RequestParam接收URL查询参数
     * - 默认值：通过defaultValue设置参数默认值
     * - 可选参数：通过required = false设置可选参数
     * 
     * MyBatis Plus分页教学：
     * - Page对象：封装分页信息（页码、每页大小、总数等）
     * - QueryWrapper：动态构建查询条件，避免SQL注入
     * - 条件构建：支持等值查询(eq)、模糊查询(like)、排序(orderBy)
     * 
     * @param page 当前页码，从1开始，默认第1页
     * @param size 每页显示数量，默认10条
     * @return 封装的分页查询结果，包含题目列表和分页信息
     */
    @GetMapping("/list")  // 映射GET请求到/api/questions/list
    @Operation(summary = "分页查询题目列表", description = "支持按分类、难度、题型、关键词进行多条件筛选的分页查询")  // Swagger接口描述
    public Result<Page<Question>> getQuestionList(
            @Parameter(description = "当前页码，从1开始", example = "1") @RequestParam(defaultValue = "1") Integer page,  // 参数描述
            @Parameter(description = "每页显示数量", example = "10") @RequestParam(defaultValue = "10") Integer size,
            // 默认情况下Knife4j为该接口生成的接口文档如下图所示，其中的queryVo参数错误会被误识别成json格式！
            QuestionQueryVo questionQueryVo) {
        // 返回统一格式的成功响应
        Page<Question> questionPage = new Page<>(page, size);
        // 方法一:嵌套查询
        // questionService.selectQuestionPage(questionPage,questionQueryVo);
        // 方法二:Java代码+Stream流处理
        questionService.selectQuestionPageStream(questionPage,questionQueryVo);
        return Result.success(questionPage);
    }
    
    /**
     * 根据ID查询单个题目详情
     * 
     * RESTful API教学：
     * - URL模式：GET /api/questions/{id}
     * - 路径参数：通过@PathVariable获取URL中的参数
     * - 语义化：URL直观表达资源和操作
     * 
     * @param id 题目主键ID，通过URL路径传递
     * @return 题目详细信息，包含选项和答案
     */
    @GetMapping("/{id}")  // {id}是路径变量，会映射到方法参数
    @Operation(summary = "根据ID查询题目详情", description = "获取指定ID的题目完整信息，包括题目内容、选项、答案等详细数据")  // API描述
    public Result<Question> getQuestionById(
            @Parameter(description = "题目ID", example = "1") @PathVariable Long id) {
            Question question = questionService.getByQuestion(id);
        return Result.success(question);
    }
    
    /**
     * 创建新题目
     * 
     * RESTful API教学：
     * - HTTP方法：POST表示创建操作
     * - 请求体：通过@RequestBody接收JSON格式的请求体
     * - 数据绑定：Spring自动将JSON转换为Java对象
     * 
     * 事务处理：
     * - 题目创建涉及多张表（题目、选项、答案）
     * - Service层方法应该使用@Transactional保证数据一致性
     * 
     * @param question 前端提交的题目数据（JSON格式）
     * @return 创建成功后的题目信息
     */
    @PostMapping  // 映射POST请求到/api/questions
    @Operation(summary = "创建新题目", description = "添加新的考试题目，支持选择题、判断题、简答题等多种题型")  // API描述
    public Result<Question> createQuestion(@RequestBody Question question) {
        questionService.addQuestion(question);
        return Result.success(question);
    }
    
    /**
     * 更新题目信息
     * 
     * RESTful API教学：
     * - HTTP方法：PUT表示更新操作
     * - URL设计：PUT /api/questions/{id} 语义明确
     * - 参数组合：路径参数(ID) + 请求体(数据)
     * 
     * @param id 要更新的题目ID
     * @param question 更新的题目数据
     * @return 更新后的题目信息
     */
    @PutMapping("/{id}")  // 处理PUT请求
    @Operation(summary = "更新题目信息", description = "修改指定题目的内容、选项、答案等信息")  // API描述
    public Result<Question> updateQuestion(
            @Parameter(description = "题目ID") @PathVariable Long id, 
            @RequestBody Question question) {
        return Result.success(null);
    }
    
    /**
     * 删除题目
     * 
     * RESTful API教学：
     * - HTTP方法：DELETE表示删除操作
     * - 响应设计：删除成功返回确认消息，失败返回错误信息
     * 
     * 注意事项：
     * - 删除前应检查题目是否被试卷引用
     * - 考虑使用逻辑删除而非物理删除，保留数据完整性
     * 
     * @param id 要删除的题目ID
     * @return 删除操作结果
     */
    @DeleteMapping("/{id}")  // 处理DELETE请求
    @Operation(summary = "删除题目", description = "根据ID删除指定的题目，包括关联的选项和答案数据")  // API描述
    public Result<String> deleteQuestion(
            @Parameter(description = "题目ID") @PathVariable Long id) {
        // 根据操作结果返回不同的响应
        if (true) {
            return Result.success("题目删除成功");
        } else {
            return Result.error("题目删除失败");
        }
    }
    
    /**
     * 根据分类查询题目列表
     * 
     * 业务场景：题目管理时按分类浏览，组卷时按分类选择题目
     * 
     * @param categoryId 分类ID
     * @return 该分类下的所有题目
     */
    @GetMapping("/category/{categoryId}")  // 处理GET请求
    @Operation(summary = "按分类查询题目", description = "获取指定分类下的所有题目列表")  // API描述
    public Result<List<Question>> getQuestionsByCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {

        return Result.success(null);
    }
    
    /**
     * 根据难度查询题目列表
     * 
     * 业务场景：按难度筛选题目，支持分层次教学
     * 
     * @param difficulty 难度等级（EASY/MEDIUM/HARD）
     * @return 指定难度的题目列表
     */
    @GetMapping("/difficulty/{difficulty}")  // 处理GET请求
    @Operation(summary = "按难度查询题目", description = "获取指定难度等级的题目列表")  // API描述
    public Result<List<Question>> getQuestionsByDifficulty(
            @Parameter(description = "难度等级，可选值：EASY(简单)/MEDIUM(中等)/HARD(困难)") @PathVariable String difficulty) {
        return Result.success(null);
    }
    
    /**
     * 随机获取题目 - 智能组卷核心功能
     * 
     * 算法思路：
     * 1. 根据条件筛选候选题目池
     * 2. 使用数据库RAND()函数或Java随机算法
     * 3. 保证题目不重复
     * 
     * 业务价值：
     * - 自动组卷：减少教师工作量
     * - 防止作弊：每次考试题目不同
     * - 个性化：根据难度和分类定制
     * 
     * @param count 需要的题目数量，默认10题
     * @param categoryId 限定分类，可选
     * @param difficulty 限定难度，可选
     * @return 随机选择的题目列表
     */
    @GetMapping("/random")  // 处理GET请求
    @Operation(summary = "随机获取题目", description = "按指定条件随机抽取题目，用于智能组卷功能")  // API描述
    public Result<List<Question>> getRandomQuestions(
            @Parameter(description = "需要获取的题目数量", example = "10") @RequestParam(defaultValue = "10") Integer count,
            @Parameter(description = "分类ID限制条件，可选") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "难度限制条件，可选值：EASY/MEDIUM/HARD") @RequestParam(required = false) String difficulty) {

        return Result.success(null);
    }

    /**
     * 获取热门题目 - 首页展示推荐题目
     * 
     * 业务逻辑：
     * - 热门度定义：按创建时间倒序，展示最新题目
     * - 可扩展：未来可按答题次数、正确率等指标排序
     * 
     * SQL优化教学：
     * - 使用LIMIT限制结果集大小，提高查询性能
     * - 建议在create_time字段上建立索引
     * 
     * @param size 返回题目数量，默认6条（适合首页展示）
     * @return 热门题目列表
     */
    /**
     * 获取热门题目 - 基于访问次数的推荐
     * 
     * 业务逻辑：
     * - 热门度定义：根据题目被访问的次数排序
     * - 实现方式：使用Redis Sorted Set记录访问次数
     * - 数据来源：用户查看题目详情时自动记录
     * 
     * 技术亮点：
     * - 使用Redis Sorted Set高效存储和排序
     * - 异步增加访问计数，不影响用户体验
     * - 缓存热门题目列表，提高查询性能
     * 
     * @param size 返回题目数量，默认10条
     * @return 热门题目列表
     */
    @GetMapping("/popular")  // 处理GET请求
    @Operation(summary = "获取热门题目", description = "获取访问次数最多的热门题目，用于首页推荐展示")  // API描述
    public Result<List<Question>> getPopularQuestions(
            @Parameter(description = "返回题目数量", example = "10") @RequestParam(defaultValue = "10") Integer size) {

        // 异常处理：记录日志并返回友好的错误信息
        return Result.error("获取热门题目失败");

    }

    /**
     * 刷新热门题目缓存 - 管理员功能
     * 
     * 业务场景：
     * - 系统初始化：首次部署时初始化热门题目数据
     * - 数据重置：清除历史访问记录，重新开始统计
     * - 手动干预：管理员可以强制更新热门题目排名
     * 
     * 技术实现：
     * - 清除缓存：删除Redis中的访问计数数据
     * - 重建缓存：为所有题目设置初始访问计数
     * - 权限控制：仅管理员可操作（前端负责控制）
     * 
     * @return 刷新结果，包含处理的题目数量
     */
    @PostMapping("/popular/refresh")
    @Operation(summary = "刷新热门题目缓存", description = "管理员功能，重置或初始化热门题目的访问计数")
    public Result<Integer> refreshPopularQuestions() {

        return Result.error("刷新热门题目缓存失败");
    }

} 