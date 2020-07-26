<template>
  <div>
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item>
        <el-input
          v-model="search"
          class="search_name"
          size="mini"
          placeholder="输入姓名查询">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button
          type="text"
          @click="onSearch()"
          class="el-icon-search">查询
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button
          class="el-icon-refresh"
          type="text"
          @click="refreshData">刷新
        </el-button>
      </el-form-item>
      <el-form-item>
        <el-button
          class="el-icon-circle-plus-outline"
          type="text"
          @click="dialogAdd = true">添加
        </el-button>
      </el-form-item>
    </el-form>

    <el-table
      :data="tableData"
      highlight-current-row
      border>
      <el-table-column
        label="编号">
        <template slot-scope="scope">
          <span>{{ scope.row.userId }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="证件类型">
        <template slot-scope="scope">
          <span>{{ scope.row.cardType }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="证件号码">
        <template slot-scope="scope">
          <span>{{ scope.row.cardNo }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="用户姓名">
        <template slot-scope="scope">
          <el-popover trigger="hover" placement="right">
            <p>证件类型: {{ scope.row.cardType }}</p>
            <p>证件号码: {{ scope.row.cardNo }}</p>
            <p>用户姓名：{{ scope.row.userName }}</p>
            <p>用户性别: {{ scope.row.userSex }}</p>
            <p>用户年龄: {{ scope.row.userAge }}</p>
            <p>用户角色：{{ scope.row.userRole }}</p>
            <div slot="reference" class="name-wrapper">
              <el-button type="text">{{ scope.row.userName }}</el-button>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column
        label="用户性别">
        <template slot-scope="scope">
          <span>{{ scope.row.userSex }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="用户年龄">
        <template slot-scope="scope">
          <span>{{ scope.row.userAge }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="用户角色">
        <template slot-scope="scope">
          <span>{{ scope.row.userRole }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="mini"
            icon="el-icon-edit"
            @click="handleEdit(scope.$index, scope.row)">编辑
          </el-button>
          <el-button
            type="text"
            size="mini"
            icon="el-icon-delete"
            @click="handleDelete(scope.$index, scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="80px" class="demo-ruleForm" size="medium">
      <el-dialog
        title="添加"
        :append-to-body='true'
        :visible.sync="dialogAdd"
        :before-close="handleClose">
        <el-form-item label="证件类型">
          <el-select v-model="ruleForm.cardType" placeholder="请选择证件类型" prop="cardType">
            <el-option label="身份证" value="身份证"></el-option>
            <el-option label="军官证" value="军官证"></el-option>
            <el-option label="护照" value="护照"></el-option>
            <el-option label="港澳居民通行证" value="港澳居民通行证"></el-option>
            <el-option label="台湾往来大陆通行证" value="台湾往来大陆通行证"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="证件号码">
          <el-input v-model="ruleForm.cardNo"></el-input>
        </el-form-item>
        <el-form-item label="用户姓名">
          <el-input v-model="ruleForm.userName"></el-input>
        </el-form-item>
        <el-form-item label="用户性别">
          <el-radio-group v-model="ruleForm.userSex">
            <el-radio label="男"></el-radio>
            <el-radio label="女"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户年龄">
          <el-slider v-model="ruleForm.userAge" show-input></el-slider>
        </el-form-item>
        <el-form-item label="用户角色">
          <el-select v-model="ruleForm.userRole" placeholder="请选择用户角色" prop="userRole">
            <el-option label="国家机关、党群组织、企业、事业单位负责人" value="国家机关、党群组织、企业、事业单位负责人"></el-option>
            <el-option label="专业技术人员" value="专业技术人员"></el-option>
            <el-option label="办事人员和有关人员" value="办事人员和有关人员"></el-option>
            <el-option label="商业、服务业人员" value="商业、服务业人员"></el-option>
            <el-option label="农、林、牧、渔、水利业生产人员" value="农、林、牧、渔、水利业生产人员"></el-option>
            <el-option label="生产、运输设备操作人员及有关人员" value="生产、运输设备操作人员及有关人员"></el-option>
            <el-option label="军人" value="军人"></el-option>
            <el-option label="不便分类的其他从业人员" value="不便分类的其他从业人员"></el-option>
            <el-option label="未知" value="未知"></el-option>
          </el-select>
        </el-form-item>

        <span slot="footer" class="dialog-footer">
            <el-button @click="emptyUserData()" size="medium">取 消</el-button>
            <el-button @click="addUser('ruleForm')" type="primary" size="medium">确 定</el-button>
          </span>
      </el-dialog>
    </el-form>

    <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="70px" class="demo-ruleForm" size="medium">
      <el-dialog
        title="编辑"
        :append-to-body='true'
        :visible.sync="dialogUpdate"
        :before-close="handleClose">
        <el-input type="hidden" v-model="ruleForm.userId"/>
        <el-form-item label="时间" prop="userDate">
          <el-date-picker type="datetime" placeholder="选择日期" v-model="ruleForm.userDate" style="width: 100%;"></el-date-picker>
        </el-form-item>
        <el-form-item label="姓名" prop="userName">
          <el-input v-model="ruleForm.userName"></el-input>
        </el-form-item>
        <el-form-item label="住址" prop="userAddress">
          <el-input v-model="ruleForm.userAddress"></el-input>
        </el-form-item>

        <span slot="footer" class="dialog-footer">
            <el-button @click="emptyUserData()" size="medium">取 消</el-button>
            <el-button @click="updateUser()" type="primary" size="medium">确 定</el-button>
          </span>
      </el-dialog>
    </el-form>
    <br>

    <el-pagination
      background
      :disabled = "disablePage"
      :current-page.sync="currentPage"
      small
      layout="prev, pager, next"
      :page-size="pageSize"
      :total="total"
      @current-change="handlePageChange">
    </el-pagination>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        ruleForm: {
          userId: null,//用户id
          cardType: null,//证件类型
          cardNo: null,//证件号码
          userName: null,//用户姓名
          userSex: null,//用户性别
          userAge: 25,//用户年龄
          userRole: null//用户角色
        },
        rules: {},
        tableData: [],
        search: '',
        dialogAdd: false,
        dialogUpdate: false,
        pageSize: 5,
        currentPage: 1,
        total: 0,
        disablePage: false
      };
    },

    created() {
      this.axios.post('/ssm_project_war_exploded/user/queryUserPage').then(response =>
      {
        this.tableData = response.data;
      }).catch(error =>
      {
        console.log(error);
      });

      this.axios.post('/ssm_project_war_exploded/user/getRowCount').then(response =>
      {
        this.total = response.data;
      }).catch(error =>
      {
        console.log(error);
      });

    },

    methods: {
      /**
       * 分页
       */
      handlePageChange() {
        //console.log(`当前页: ${this.currentPage}`);
        let postData = this.qs.stringify({
          page: this.currentPage
        });
        this.axios({
          method: 'post',
          url:'/ssm_project_war_exploded/user/queryUserPage',
          data:postData
        }).then(response =>
        {
          this.tableData = response.data;
        }).catch(error =>
        {
          console.log(error);
        });
      },

      /**
       * 添加用户
       */
      addUser() {
        if(this.ruleForm.cardType == null || this.ruleForm.cardNo == null || this.ruleForm.userName == null || this.ruleForm.userSex == null || this.ruleForm.userRole == null){
          this.$alert('用户信息不完整请检查', '温馨提示', {
            confirmButtonText: '确定'
          });
          return;
        }
        let postData = this.qs.stringify({
          cardType: this.ruleForm.cardType,//证件类型
          cardNo: this.ruleForm.cardNo,//证件号码
          userName: this.ruleForm.userName,//用户姓名
          userSex: this.ruleForm.userSex,//用户性别
          userAge: this.ruleForm.userAge,//用户年龄
          userRole: this.ruleForm.userRole,//用户角色
        });
        this.axios({
          method: 'post',
          url:'/ssm_project_war_exploded/user/createUser',
          data:postData
        }).then(response =>
        {
          this.handlePageChange();
          this.getRowCount();
          this.$message({
            type: 'success',
            message: '已添加!'
          });
          this.emptyUserData();
          //console.log(response);
        }).catch(error =>
        {
          console.log(error);
        });
      },

      /**
       * 统计用户个数
       */
      getRowCount() {
        this.axios.post('/ssm_project_war_exploded/user/getRowCount').then(response =>
        {
          this.total = response.data;
        }).catch(error =>
        {
          console.log(error);
        });
      },

      handleEdit(index, row) {
        this.dialogUpdate = true;
        this.ruleForm = Object.assign({}, row); //这句是关键！！！
      },

      handleClose(done) {
        this.$confirm('确认关闭？')
          .then(_ => {
            done();
            this.emptyUserData();
          })
          .catch(_ => {});
      },

      /**
       * 清空绑定数据
       */
      emptyUserData() {
        this.dialogAdd = false;
        this.dialogUpdate = false;
        this.ruleForm = {
          userId: null,//用户id
          cardType: null,//证件类型
          cardNo: null,//证件号码
          userName: null,//用户姓名
          userSex: null,//用户性别
          userAge: 25,//用户年龄
          userRole: null//用户角色
        };
      },

      /**
       * 根据 userId 删除用户
       * @param index
       * @param row
       */
      handleDelete(index, row) {
        //console.log(index, row);
        this.$confirm('删除操作, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let postData = this.qs.stringify({
            userId: row.userId,
          });
          this.axios({
            method: 'post',
            url:'/ssm_project_war_exploded/user/deleteUserById',
            data:postData
          }).then(response =>
          {
            this.getRowCount();
            if(this.total%5==1 && this.currentPage >= 1){
              if(this.total/5<this.currentPage){
                this.currentPage = this.currentPage-1;
              }
            }
            this.handlePageChange();

            this.$message({
              type: 'success',
              message: '删除成功!'
            });
            //console.log(response);
          }).catch(error =>
          {
            console.log(error);
          });

        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },


      updateUser() {
        let postData = this.qs.stringify({
          userId: this.ruleForm.userId,
          userDate: this.ruleForm.userDate,
          userName: this.ruleForm.userName,
          userAddress: this.ruleForm.userAddress
        });
        this.axios({
          method: 'post',
          url:'/update',
          data:postData
        }).then(response =>
        {
          this.handleCurrentChange();
          this.cancel();
          this.$message({
            type: 'success',
            message: '更新成功!'
          });
          console.log(response);
        }).catch(error =>
        {
          this.$message({
            type: 'success',
            message: '更新失败!'
          });
          console.log(error);
        });
      },
      onSearch() {
        let postData = this.qs.stringify({
          userName: this.search
        });
        this.axios({
          method: 'post',
          url: '/ListByName',
          data: postData
        }).then(response =>
        {
          this.tableData = response.data;
          this.disablePage = true;
        }).catch(error =>
        {
          console.log(error);
        });
      },

      refreshData() {
        location.reload();
      }
    },

  }
</script>
<style scoped>

</style>
