package jp.co.sss.shop.controller.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;


/**
 * 会員管理 削除機能(一般会員)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class UserDeleteCustomerController {


	/**
	 * 会員情報
	 */
	@Autowired
	UserRepository userRpository;

	/**
	 * 会員情報削除確認処理
	 *
	 * @param model Viewとの値受渡し
	 * @param form 会員情報フォーム
	 * @return "user/delete/user_delete_check" 会員情報 削除確認画面へ
	 */
	@RequestMapping(path = "/user/delete/check", method = RequestMethod.POST)
	public String deleteCheck(Model model, @ModelAttribute UserForm form) {

		// 削除対象の会員情報を取得
		User user = userRpository.findById(form.getId()).orElse(null);

		UserBean userBean = new UserBean();

		// Userエンティティの各フィールドの値をUserBeanにコピー
		BeanUtils.copyProperties(user, userBean);

		// 会員情報をViewに渡す
		model.addAttribute("user", userBean);

		return "user/delete/user_delete_check";
	}

	/**
	 * 会員情報削除完了処理
	 *
	 * @param form 会員情報フォーム
	 * @param session セッション情報
	 * @return "user/delete/user_delete_complete" 会員情報 削除完了画面へ
	 */
	@RequestMapping(path = "/user/delete/complete", method = RequestMethod.POST)
	public String deleteComplete(@ModelAttribute UserForm form, HttpSession session) {

		// 削除対象の会員情報を取得
		User user = userRpository.findById(form.getId()).orElse(null);
		// 削除フラグを立てる
		user.setDeleteFlag(Constant.DELETED);
		// 会員情報を保存
		userRpository.save(user);
		// セッション情報を削除する
		session.invalidate();

		return "user/delete/user_delete_complete";
	}
}
