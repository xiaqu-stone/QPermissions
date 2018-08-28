package com.stone.qpermission

import android.content.Context
import android.support.annotation.StringRes
import com.stone.mdlib.MDAlert
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission

/**
 * Created By: sqq
 * Created Time: 8/20/18 7:20 PM.
 *
 * 基于AndPermission的权限快速调用工具类封装
 */


/**
 * @param permissions 需要申请的权限列表
 * @param deniedMsg 被拒时的弹框解释文案
 * @param rationaleMag 被拒过一次再次申请时的解释文案
 * @param callback 权限申请成功后的回调
 */
@JvmOverloads
inline fun Context.reqPermissions(vararg permissions: String, deniedMsg: String? = null, rationaleMag: String? = null, crossinline callback: () -> Unit) {
    AndPermission.with(this).runtime()
            .permission(permissions)
            .onDenied {
                if (AndPermission.hasAlwaysDeniedPermission(this, it)) {
                    showAlert(deniedMsg
                            ?: "因缺少相应的权限，下一步的功能无法使用，请去设置中授权权限: \n${Permission.transformText(this, it).joinToString("，\n")}") { AndPermission.with(this).runtime().setting().start() }
                }
            }
            .onGranted { callback.invoke() }
            .rationale { ctx, pList, executor ->
                showAlert(rationaleMag
                        ?: "为保证功能的正常使用，请允许应用访问您的权限：\n${Permission.transformText(ctx, pList).joinToString("，\n")}") { executor.execute() }
            }.start()
}


inline fun Context.reqPermissions(vararg permissions: String, @StringRes deniedMsg: Int? = null, @StringRes rationaleMag: Int?, crossinline callback: () -> Unit) {
    AndPermission.with(this).runtime()
            .permission(permissions)
            .onDenied {
                if (AndPermission.hasAlwaysDeniedPermission(this, it)) {
                    showAlert((if (deniedMsg == null) null else this.getString(deniedMsg))
                            ?: "因缺少相应的权限，下一步的功能无法使用，请去设置中授权权限: \n${Permission.transformText(this, it).joinToString("，\n")}") { AndPermission.with(this).runtime().setting().start() }
                }
            }
            .onGranted { callback.invoke() }
            .rationale { ctx, pList, executor ->
                showAlert((if (rationaleMag == null) null else this.getString(rationaleMag))
                        ?: "为保证功能的正常使用，请允许应用访问您的权限：\n${Permission.transformText(ctx, pList).joinToString("，\n")}") { executor.execute() }
            }.start()
}

inline fun Context.showAlert(msg: String, title: String = "权限提示：", crossinline callback: () -> Unit) {
    MDAlert(this, msg).setTitle(title).setCancel().setBtnPositive { callback.invoke() }.show()
}
