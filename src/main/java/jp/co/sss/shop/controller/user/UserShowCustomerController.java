package jp.co.sss.shop.controller.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員管理 表示機能(一般会員)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class UserShowCustomerController {
	/**
	 * 会員情報
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * 会員情報詳細画面表示処理
	 *
	 * @param model
	 *            Viewとの値受渡し
	 * @param session
	 *            セッション情報
	 * @return "user/regist/user_regist_input" 会員情報 登録入力画面へ
	 */

	@RequestMapping(path = "/user/detail")
	public String showUser(Model model, HttpSession session) {

		// ログインしている会員IDを取得する
		Integer id = ((UserBean) session.getAttribute("user")).getId();

		// 会員情報を取得する
		User user = userRepository.findById(id).orElse(null);

		UserBean userBean = new UserBean();

		// Userエンティティの各フィールドの値をUserBeanにコピー
		BeanUtils.copyProperties(user, userBean);

		// 会員情報をViewに渡す
		model.addAttribute("user", userBean);

		return "user/detail/user_detail";
	}
}
