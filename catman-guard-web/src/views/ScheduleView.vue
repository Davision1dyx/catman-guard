<template>
  <div class="schedule-container">
    <Sidebar />
    <main class="schedule-main">
      <!-- 顶部状态栏 -->
      <TopHeader title="值班排班">
        <template #search>
          <div class="search-box">
            <el-input 
              v-model="searchQuery" 
              placeholder="搜索值班人员或组别..." 
              prefix-icon="Search"
              class="search-input"
            />
          </div>
        </template>
        <template #actions>
          <button class="notification-btn" @click="toggleNotifications">
            <el-icon><Bell /></el-icon>
            <span class="badge">3</span>
          </button>
        </template>
      </TopHeader>

      <!-- 主工作区 -->
      <main class="schedule-content">
        <!-- 页面头部 -->
        <div class="page-header">
          <div>
            <h1 class="page-title">值班排班</h1>
            <p class="page-subtitle">管理系统中的值班人员及其排班安排</p>
          </div>
          <div class="header-actions">
            <el-button type="primary" @click="openAddStaffModal">
              <el-icon><Plus /></el-icon>
              添加值班人员
            </el-button>
            <el-button type="success" @click="generateSchedule">
              <el-icon><Refresh /></el-icon>
              生成排班
            </el-button>
          </div>
        </div>

        <!-- 值班人员管理 -->
        <div class="staff-panel">
          <div class="panel-header">
            <h2 class="panel-title">值班人员管理</h2>
            <div class="panel-actions">
              <el-button type="info" @click="openAddGroupModal">
                <el-icon><Users /></el-icon>
                添加分组
              </el-button>
            </div>
          </div>
          
          <!-- 值班人员表格 -->
          <el-table 
            :data="filteredStaff" 
            border 
            style="width: 100%"
            :empty-text="'暂无值班人员'"
          >
            <el-table-column label="姓名" width="160">
              <template #default="scope">
                <div class="staff-info">
                  <div class="avatar">
                    <el-icon><User /></el-icon>
                  </div>
                  <span>{{ scope.row.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="email" label="邮箱" width="200" />
            <el-table-column prop="phone" label="电话" width="140" />
            <el-table-column prop="groupName" label="所属组别" width="120">
              <template #default="scope">
                <el-tag type="purple">{{ scope.row.groupName || '未分组' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStaffStatusTagType(scope.row.status)">
                  {{ getStaffStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="joinedAt" label="加入时间" width="140">
              <template #default="scope">{{ formatDate(scope.row.joinedAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <div class="action-buttons">
                  <el-button size="small" icon="Edit" @click="editStaff(scope.row)" />
                  <el-button size="small" icon="Delete" type="danger" @click="deleteStaff(scope.row.id)" />
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 排班日历 -->
        <div class="calendar-panel">
          <div class="panel-header">
            <h2 class="panel-title">排班日历</h2>
            <div class="calendar-nav">
              <el-button @click="prevWeek" size="small">
                <el-icon><ArrowLeft /></el-icon>
                上一周
              </el-button>
              <span class="current-week">{{ currentWeekDisplay }}</span>
              <el-button @click="nextWeek" size="small">
                下一周
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
          
          <!-- 日历视图 -->
          <div class="calendar-grid">
            <div 
              v-for="(day, index) in weekDays" 
              :key="day.date.toString()"
              class="calendar-day"
            >
              <div class="day-header">
                <div class="day-name">{{ day.dayOfWeek }}</div>
                <div class="day-number">{{ day.day }}</div>
                <div class="day-month">{{ day.monthYear }}</div>
              </div>
              
              <div class="day-content">
                <div 
                  v-for="schedule in getSchedulesForDay(day.date)" 
                  :key="schedule.id"
                  class="schedule-item"
                >
                  <div class="schedule-group">{{ schedule.groupName }}</div>
                  <div class="schedule-members">
                    <div 
                      v-for="member in getGroupMembers(schedule.groupId)" 
                      :key="member.id"
                      class="member-item"
                    >
                      <el-icon><User /></el-icon>
                      <span>{{ member.name }}</span>
                    </div>
                  </div>
                </div>
                
                <div v-if="getSchedulesForDay(day.date).length === 0" class="empty-schedule">
                  暂无排班
                </div>
              </div>
            </div>
          </div>
          
          <!-- 分组信息 -->
          <div class="groups-section">
            <h3 class="groups-title">分组信息</h3>
            <div class="groups-grid">
              <div 
                v-for="group in groups" 
                :key="group.id"
                class="group-card"
              >
                <div class="group-header">
                  <span class="group-name">{{ group.name }}</span>
                  <span class="group-count">{{ group.members.length }} 人</span>
                </div>
                <div class="group-members">
                  <div 
                    v-for="member in group.members" 
                    :key="member.id"
                    class="group-member"
                  >
                    <el-icon><User /></el-icon>
                    <span>{{ member.name }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>

      <!-- 添加值班人员模态框 -->
      <el-dialog 
        v-model="showAddStaffModal" 
        :title="editingStaff ? '编辑值班人员' : '添加值班人员'" 
        width="400px"
        :close-on-click-modal="false"
      >
        <form v-if="newStaff" @submit.prevent="saveStaff">
          <div class="form-item">
            <label>姓名</label>
            <el-input v-model="newStaff.name" required />
          </div>
          <div class="form-item">
            <label>邮箱</label>
            <el-input v-model="newStaff.email" type="email" required />
          </div>
          <div class="form-item">
            <label>电话</label>
            <el-input v-model="newStaff.phone" type="tel" required />
          </div>
          <div class="form-item">
            <label>所属组别</label>
            <el-select v-model="newStaff.groupId">
              <el-option value="" label="请选择组别" />
              <el-option v-for="group in groups" :key="group.id" :value="group.id">
                {{ group.name }}
              </el-option>
            </el-select>
          </div>
          <div class="form-item">
            <label>状态</label>
            <el-select v-model="newStaff.status">
              <el-option value="ACTIVE" label="在职" />
              <el-option value="ON_LEAVE" label="请假" />
              <el-option value="INACTIVE" label="离职" />
            </el-select>
          </div>
        </form>

        <template #footer>
          <el-button @click="closeAddStaffModal">取消</el-button>
          <el-button type="primary" @click="saveStaff">保存</el-button>
        </template>
      </el-dialog>

      <!-- 添加分组模态框 -->
      <el-dialog 
        v-model="showAddGroupModal" 
        title="添加分组" 
        width="400px"
        :close-on-click-modal="false"
      >
        <form @submit.prevent="saveGroup">
          <div class="form-item">
            <label>组别名称</label>
            <el-input v-model="newGroup.name" required />
          </div>
        </form>

        <template #footer>
          <el-button @click="closeAddGroupModal">取消</el-button>
          <el-button type="primary" @click="saveGroup">保存</el-button>
        </template>
      </el-dialog>
    </main>
  </div>
</template>

<script setup lang="ts">import { ref, computed } from 'vue';
import Sidebar from '../components/Sidebar.vue';
import TopHeader from '../components/TopHeader.vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ArrowLeft, Search, Bell, Plus, Refresh, User, Edit, Delete, UsersFilled } from '@element-plus/icons-vue';
// 值班人员接口定义
interface Staff {
 id: string;
 name: string;
 email: string;
 phone: string;
 groupId: string | null;
 groupName: string | null;
 status: 'ACTIVE' | 'ON_LEAVE' | 'INACTIVE';
 joinedAt: Date;
}
// 分组接口定义
interface Group {
 id: string;
 name: string;
 members: Staff[];
}
// 排班接口定义
interface Schedule {
 id: string;
 groupId: string;
 groupName: string;
 startDate: Date;
 endDate: Date;
 isActive: boolean;
}
// 周日期接口定义
interface WeekDay {
 date: Date;
 day: number;
 monthYear: string;
 dayOfWeek: string;
}
// 响应式数据
const showAddStaffModal = ref(false);
const showAddGroupModal = ref(false);
const editingStaff = ref(false);
const newStaff = ref<Partial<Staff>>({
 name: '',
 email: '',
 phone: '',
 groupId: null,
 status: 'ACTIVE'
});
const newGroup = ref({
 name: ''
});
// 筛选条件
const searchQuery = ref('');
// 当前周的起始日期
const currentWeekStart = ref(new Date());
// 模拟值班人员数据
const staffList = ref<Staff[]>([
 {
 id: 'staff-1',
 name: '张伟',
 email: 'zhangwei@example.com',
 phone: '13800138001',
 groupId: 'group-1',
 groupName: '第一组',
 status: 'ACTIVE',
 joinedAt: new Date(Date.now() - 86400000 * 30)
 },
 {
 id: 'staff-2',
 name: '李娜',
 email: 'lina@example.com',
 phone: '13800138002',
 groupId: 'group-1',
 groupName: '第一组',
 status: 'ACTIVE',
 joinedAt: new Date(Date.now() - 86400000 * 25)
 },
 {
 id: 'staff-3',
 name: '王强',
 email: 'wangqiang@example.com',
 phone: '13800138003',
 groupId: 'group-2',
 groupName: '第二组',
 status: 'ACTIVE',
 joinedAt: new Date(Date.now() - 86400000 * 20)
 },
 {
 id: 'staff-4',
 name: '陈丽',
 email: 'chenli@example.com',
 phone: '13800138004',
 groupId: 'group-2',
 groupName: '第二组',
 status: 'ACTIVE',
 joinedAt: new Date(Date.now() - 86400000 * 15)
 },
 {
 id: 'staff-5',
 name: '赵刚',
 email: 'zhaogang@example.com',
 phone: '13800138005',
 groupId: 'group-3',
 groupName: '第三组',
 status: 'ACTIVE',
 joinedAt: new Date(Date.now() - 86400000 * 10)
 },
 {
 id: 'staff-6',
 name: '刘敏',
 email: 'liumin@example.com',
 phone: '13800138006',
 groupId: 'group-3',
 groupName: '第三组',
 status: 'ON_LEAVE',
 joinedAt: new Date(Date.now() - 86400000 * 5)
 }
]);
// 模拟分组数据
const groups = ref<Group[]>([
 {
 id: 'group-1',
 name: '第一组',
 members: []
 },
 {
 id: 'group-2',
 name: '第二组',
 members: []
 },
 {
 id: 'group-3',
 name: '第三组',
 members: []
 }
]);
// 模拟排班数据
const schedules = ref<Schedule[]>([
 {
 id: 'sched-1',
 groupId: 'group-1',
 groupName: '第一组',
 startDate: new Date(Date.now() - 86400000 * 7),
 endDate: new Date(Date.now()),
 isActive: false
 },
 {
 id: 'sched-2',
 groupId: 'group-2',
 groupName: '第二组',
 startDate: new Date(Date.now()),
 endDate: new Date(Date.now() + 86400000 * 7),
 isActive: true
 },
 {
 id: 'sched-3',
 groupId: 'group-3',
 groupName: '第三组',
 startDate: new Date(Date.now() + 86400000 * 7),
 endDate: new Date(Date.now() + 86400000 * 14),
 isActive: false
 }
]);
// 计算属性：筛选后的值班人员
const filteredStaff = computed(() => {
 return staffList.value.filter(staff => {
 const matchesSearch = !searchQuery.value ||
 staff.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
 staff.email.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
 staff.phone.includes(searchQuery.value) ||
 (staff.groupName && staff.groupName.toLowerCase().includes(searchQuery.value.toLowerCase()));
 return matchesSearch;
 });
});
// 计算属性：本周日期
const weekDays = computed<WeekDay[]>(() => {
 const start = new Date(currentWeekStart.value);
 const days: WeekDay[] = [];
 for (let i = 0; i < 7; i++) {
 const date = new Date(start);
 date.setDate(start.getDate() + i);
 const dayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
 const monthNames = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
 days.push({
 date,
 day: date.getDate(),
 monthYear: `${monthNames[date.getMonth()]} ${date.getFullYear()}`,
 dayOfWeek: dayNames[date.getDay()]
 });
 }
 return days;
});
// 计算属性：当前周显示文本
const currentWeekDisplay = computed(() => {
 const start = weekDays.value[0];
 const end = weekDays.value[6];
 return `${start.monthYear} ${start.day}日 - ${end.day}日`;
});
// 获取指定日期的排班信息
const getSchedulesForDay = (date: Date) => {
 return schedules.value.filter(schedule => {
 return date >= schedule.startDate && date <= schedule.endDate;
 });
};
// 获取指定组的成员
const getGroupMembers = (groupId: string) => {
 return staffList.value.filter(staff => staff.groupId === groupId);
};
// 获取值班人员状态文本
const getStaffStatusText = (status: string) => {
 switch (status) {
 case 'ACTIVE':
 return '在职';
 case 'ON_LEAVE':
 return '请假';
 case 'INACTIVE':
 return '离职';
 default:
 return status;
 }
};
// 获取值班人员状态标签类型
const getStaffStatusTagType = (status: string) => {
 switch (status) {
 case 'ACTIVE':
 return 'success';
 case 'ON_LEAVE':
 return 'warning';
 case 'INACTIVE':
 return 'info';
 default:
 return 'info';
 }
};
// 格式化日期
const formatDate = (date: Date) => {
 return new Date(date).toLocaleDateString('zh-CN');
};
// 打开添加值班人员模态框
const openAddStaffModal = () => {
 editingStaff.value = false;
 newStaff.value = {
 name: '',
 email: '',
 phone: '',
 groupId: null,
 status: 'ACTIVE'
 };
 showAddStaffModal.value = true;
};
// 编辑值班人员
const editStaff = (staff: Staff) => {
 editingStaff.value = true;
 newStaff.value = { ...staff };
 showAddStaffModal.value = true;
};
// 保存值班人员
const saveStaff = () => {
 if (!newStaff.value.name || !newStaff.value.email || !newStaff.value.phone) {
 ElMessage.warning('请填写完整信息');
 return;
 }
 if (editingStaff.value) {
 const index = staffList.value.findIndex(s => s.id === newStaff.value.id);
 if (index !== -1) {
 staffList.value[index] = {
 ...newStaff.value,
 id: staffList.value[index].id,
 groupName: groups.value.find(g => g.id === newStaff.value.groupId)?.name || null,
 joinedAt: staffList.value[index].joinedAt
 } as Staff;
 }
 }
 else {
 const newId = `staff-${Date.now()}`;
 staffList.value.push({
 id: newId,
 name: newStaff.value.name!,
 email: newStaff.value.email!,
 phone: newStaff.value.phone!,
 groupId: newStaff.value.groupId || null,
 groupName: newStaff.value.groupId ? groups.value.find(g => g.id === newStaff.value.groupId)?.name || null : null,
 status: newStaff.value.status as 'ACTIVE' | 'ON_LEAVE' | 'INACTIVE',
 joinedAt: new Date()
 });
 }
 closeAddStaffModal();
 ElMessage.success('保存成功');
};
// 删除值班人员
const deleteStaff = (id: string) => {
 ElMessageBox.confirm('确定要删除这个值班人员吗？', '提示', {
 confirmButtonText: '确定',
 cancelButtonText: '取消',
 type: 'warning'
 }).then(() => {
 staffList.value = staffList.value.filter(staff => staff.id !== id);
 ElMessage.success('删除成功');
 }).catch(() => {
 ElMessage.info('已取消删除');
 });
};
// 关闭添加值班人员模态框
const closeAddStaffModal = () => {
 showAddStaffModal.value = false;
 editingStaff.value = false;
 newStaff.value = {
 name: '',
 email: '',
 phone: '',
 groupId: null,
 status: 'ACTIVE'
 };
};
// 打开添加分组模态框
const openAddGroupModal = () => {
 newGroup.value = { name: '' };
 showAddGroupModal.value = true;
};
// 保存分组
const saveGroup = () => {
 if (!newGroup.value.name) {
 ElMessage.warning('请输入组别名称');
 return;
 }
 const existingGroup = groups.value.find(g => g.name === newGroup.value.name);
 if (existingGroup) {
 ElMessage.warning('组别名称已存在');
 return;
 }
 const newId = `group-${Date.now()}`;
 groups.value.push({
 id: newId,
 name: newGroup.value.name,
 members: []
 });
 closeAddGroupModal();
 ElMessage.success('添加成功');
};
// 关闭添加分组模态框
const closeAddGroupModal = () => {
 showAddGroupModal.value = false;
 newGroup.value = { name: '' };
};
// 上一周
const prevWeek = () => {
 const newDate = new Date(currentWeekStart.value);
 newDate.setDate(newDate.getDate() - 7);
 currentWeekStart.value = newDate;
};
// 下一周
const nextWeek = () => {
 const newDate = new Date(currentWeekStart.value);
 newDate.setDate(newDate.getDate() + 7);
 currentWeekStart.value = newDate;
};
// 生成排班
const generateSchedule = () => {
 if (groups.value.length === 0) {
 ElMessage.warning('请先添加分组');
 return;
 }
 if (staffList.value.length === 0) {
 ElMessage.warning('请先添加值班人员');
 return;
 }
 schedules.value = [];
 for (let i = 0; i < groups.value.length; i++) {
 const group = groups.value[i];
 const startDate = new Date(Date.now() + 86400000 * (i * 7));
 const endDate = new Date(startDate.getTime() + 86400000 * 7);
 schedules.value.push({
 id: `sched-${Date.now()}-${i}`,
 groupId: group.id,
 groupName: group.name,
 startDate,
 endDate,
 isActive: i === 0
 });
 }
 ElMessage.success('排班已生成');
};
// 初始化分组成员
const initializeGroupMembers = () => {
 groups.value.forEach(group => {
 group.members = staffList.value.filter(staff => staff.groupId === group.id);
 });
};
initializeGroupMembers();
const toggleNotifications = () => {
 ElMessage.info('通知中心功能开发中');
};
</script>

<style scoped>
.schedule-container {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}

.schedule-main {
  flex: 1;
  margin-left: 250px;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.schedule-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-left .breadcrumb {
  display: flex;
  align-items: center;
}

.header-left .breadcrumb ol {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
}

.header-left .breadcrumb li {
  display: flex;
  align-items: center;
  color: #666;
  font-size: 14px;
}

.header-left .breadcrumb li:first-child {
  color: #999;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-box {
  position: relative;
}

.search-input {
  width: 280px;
}

.notification-btn {
  position: relative;
  padding: 8px;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #666;
  font-size: 18px;
}

.notification-btn:hover {
  color: #409eff;
}

.notification-btn .badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  background: #f56c6c;
  color: #fff;
  font-size: 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.schedule-content {
  flex: 1;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.page-subtitle {
  color: #999;
  font-size: 14px;
  margin: 8px 0 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.staff-panel, .calendar-panel {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 20px;
  margin-bottom: 24px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.panel-actions {
  display: flex;
  gap: 12px;
}

.staff-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 36px;
  height: 36px;
  background: #e6f7ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1890ff;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.calendar-nav {
  display: flex;
  align-items: center;
  gap: 16px;
}

.current-week {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 16px;
}

.calendar-day {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.day-header {
  text-align: center;
  padding: 12px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
}

.day-name {
  font-size: 13px;
  color: #999;
  margin-bottom: 4px;
}

.day-number {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.day-month {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.day-content {
  padding: 12px;
  min-height: 120px;
}

.schedule-item {
  background: #e6f7ff;
  border-radius: 6px;
  padding: 10px;
  margin-bottom: 8px;
}

.schedule-group {
  font-weight: 500;
  color: #333;
  font-size: 13px;
}

.schedule-members {
  margin-top: 8px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.member-item el-icon {
  font-size: 14px;
  color: #1890ff;
}

.empty-schedule {
  text-align: center;
  color: #999;
  font-size: 13px;
  padding: 20px 0;
}

.groups-section {
  margin-top: 32px;
}

.groups-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px;
}

.groups-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.group-card {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 16px;
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.group-name {
  font-weight: 500;
  color: #333;
}

.group-count {
  font-size: 13px;
  color: #999;
}

.group-members {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.group-member {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.group-member el-icon {
  color: #1890ff;
}

.form-item {
  margin-bottom: 16px;
}

.form-item label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  margin-bottom: 8px;
}
</style>