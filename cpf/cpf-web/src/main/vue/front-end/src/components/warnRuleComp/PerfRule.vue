<template>
<div>
    <el-form :rules="rules" :model="data.config" ref="ruleForm">
    <el-row>
      <el-col :span="20">
        <el-form-item label="名称" prop="name" >
          <el-input v-model="data.config.name" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item label="host名称">
          <el-input v-model="data.config.host" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item label="持续时间" prop="time" >
          <el-input v-model="data.config.time" auto-complete="off" placeholder="单位为分钟，为空代表立即报警"></el-input>
        </el-form-item>
        <el-form-item label="MB_Received_persec最大值" prop="Bytes_Received_persec">
            <el-input v-model="data.config.Bytes_Received_persec"  placeholder="单位为MB，正浮点数，为空条件不限制"></el-input>
        </el-form-item>
        <el-form-item label="MB_Sent_persec最大值" prop="Bytes_Sent_persec">
            <el-input v-model="data.config.Bytes_Sent_persec" placeholder="单位为MB，正浮点数，为空条件不限制"></el-input>
        </el-form-item>    
        <el-form-item label="Packets_Outbound_Discarded最大值" prop="Packets_Outbound_Discarded">
            <el-input v-model="data.config.Packets_Outbound_Discarded"  placeholder="正整数，为空条件不限制"></el-input>
        </el-form-item> 
        <el-form-item label="Packets_Outbound_Errors最大值" prop="Packets_Outbound_Errors">
            <el-input v-model="data.config.Packets_Outbound_Errors"  placeholder="正整数，为空条件不限制"></el-input>
        </el-form-item> 
        <el-form-item label="Packets_Received_Discarded最大值" prop="Packets_Received_Discarded">
            <el-input v-model="data.config.Packets_Received_Discarded"  placeholder="正整数，为空条件不限制"></el-input>
        </el-form-item>
        <el-form-item label="Packets_Received_ErrorsB最大值" prop="Packets_Received_Errors">
            <el-input v-model="data.config.Packets_Received_Errors"  placeholder="正整数，为空条件不限制"></el-input>
        </el-form-item> 
        <el-form-item label="Packets_Received_persec最大值" prop="Packets_Received_persec">
            <el-input v-model="data.config.Packets_Received_persec"  placeholder="正浮点数，为空条件不限制"></el-input>
        </el-form-item>
        <el-form-item label="Packets_Sent_persec最大值" prop="Packets_Sent_persec">
            <el-input v-model="data.config.Packets_Sent_persec"  placeholder="正浮点数，为空条件不限制"></el-input>
        </el-form-item>                                 
      </el-col>
    </el-row>
  </el-form>
    <div slot="footer" class="dialog-footer">
    <el-button @click="closeDialog">取 消</el-button>
    <el-button type="primary" @click="submit">确 定</el-button>
  </div>
</div>
</template>
<script>
import { mapState } from "vuex";
import {saveRule} from "@/api/getData";
import {checkPercent,checkInteger,checkDouble} from "@/utils/validate"
export default {
  name: "PerfRule",
  data() {
    return {
      rules: {
        Bytes_Received_persec: [{ validator: checkDouble, trigger: "blur" }],
        Bytes_Sent_persec: [{ validator: checkDouble, trigger: "blur" }],
        Packets_Outbound_Discarded: [{ validator: checkInteger, trigger: "blur" }],
        Packets_Outbound_Errors: [{ validator: checkInteger, trigger: "blur" }],
        Packets_Received_Discarded: [{ validator: checkInteger, trigger: "blur" }],
        Packets_Received_Errors: [{ validator: checkInteger, trigger: "blur" }],
        Packets_Received_persec: [{ validator: checkDouble, trigger: "blur" }],
        Packets_Sent_persec: [{ validator: checkDouble, trigger: "blur" }],
        time:[{ validator: checkInteger, trigger: "blur" }],
        name:[{required:true,message:'不能为空', trigger: 'blur'}]
      }
    };
  },
  computed:{
    Bytes_Received_persec(){
      
      return this.data.config.Bytes_Received_persec*(2**20);
    },
    Bytes_Sent_persec(){
      return this.data.config.Bytes_Sent_persec*(2**20);
    }
  },
  methods: {
     submit() {
      this.$refs.ruleForm.validate((valid)=>{
        if(valid){
          this.saveModel();
        }else{

        }
      });
    },
    closeDialog() {
      this.$store.commit("closeRuleAddDialog");
    },
    async saveModel() {
      this.data.name = this.data.config.name;
      this.data.time = this.data.config.time;
      this.data.config.Bytes_Received_persec = this.Bytes_Received_persec;
      this.data.config.Bytes_Sent_persec = this.Bytes_Sent_persec;
      const response = await saveRule(this.data);
      if(response.success){
        this.$emit("saveRule",response.result);
        this.$message('保存规则成功');
        this.closeDialog();
      }else{
        this.$message('保存规则失败');
      }
    }
  },
  props: {
    data: {}
  },
  created(){
    if(this.data.config == undefined){
      //elementUI 表单校验只能校验单层属性，所以将name放在config层
      this.data.config = {name:this.data.name};
    }
  }
};
</script>
