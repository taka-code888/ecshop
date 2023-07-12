package jp.co.sss.shop.controller.basket;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.form.BasketForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.util.BeanCopy;

/**
 * 買い物かご機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class BasketCustomerController {

	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 商品を買い物かごに追加する処理
	 *
	 * @param form  買い物かごフォーム
	 * @param model Viewとの値受渡し
	 * @return ログインしている "redirect:/basket/shopping_basket" 注文一覧1ページ目 / ログインしていない
	 *         "forward:/login" ログイン画面
	 * @since 1.4 / 2016/07/06
	 */
	@RequestMapping(path = "/basket/add", method = RequestMethod.POST)
	public String addItem(@ModelAttribute BasketForm form, Model model) {

		@SuppressWarnings("unchecked")
		List<BasketBean> basketBeanList = ((List<BasketBean>) session.getAttribute("basketBeans"));

		// フォームで追加された商品情報を取得
		ItemBean item = BeanCopy.copyEntityToBean(itemRepository.getOne(form.getId()));

		// 買い物かごが空だった場合、新しくリストを作成
		if (basketBeanList == null) {
			basketBeanList = new ArrayList<BasketBean>();
		}

		// すでに買い物かごに入れられている商品か否かを調べる
		boolean isSkip = false;
		for (BasketBean basketBean : basketBeanList) {
			if (basketBean.getId() == item.getId()) {
				// 注文ID(item.getId())が買い物かご内の商品IDと一致していた場合、処理を飛ばすフラグを立てる
				isSkip = true;

				if (item.getStock() > basketBean.getOrderNum()) {
					// 買い物かごに追加した時点での在庫数が現在の注文個数より多い場合、注文個数を1増加する
					basketBean.setOrderNum(basketBean.getOrderNum() + 1);
				} else {
					// 買い物かごに追加した時点での在庫数が現在の注文個数より少ない場合、
					// 在庫切れのメッセージ用に商品名をリクエストスコープに保存する
					model.addAttribute("errorItem", item.getName());
				}
				break;
			}
		}

		// 処理を飛ばすフラグが立っていなかったら、商品情報を買い物かごに追加する
		if (!isSkip) {

			if (item.getStock() > 0) {
				// 現在の在庫数が1以上の場合、商品情報を買い物かごに追加する。
				BasketBean basketBean = new BasketBean(item.getId(), item.getName(), item.getStock());
				// 買い物かごの商品を追加する（商品は新しく追加した順に表示するため、先頭に追加する）
				basketBeanList.add(0, basketBean);
				session.setAttribute("basketBeans", basketBeanList);
			} else {
				// 在庫が0の場合、在庫切れのメッセージ用に商品名をリクエストスコープに保存する
				model.addAttribute("errorItem",item.getName());
			}
		}

		// 買い物かご内の商品一覧を表示
		return "forward:/basket/list";
	}

	/**
	 * 買い物かごの商品個数を1減らし、0個になる場合削除する。
	 *
	 * @param form 買い物かごフォーム
	 * @return "forward:/basket/list" 注文一覧1ページ目
	 */
	@RequestMapping(path = "/basket/delete", method = RequestMethod.POST)
	public String subtractCountItem(@ModelAttribute BasketForm form) {

		@SuppressWarnings("unchecked")
		List<BasketBean> basketBeanList = ((List<BasketBean>) session.getAttribute("basketBeans"));

		BasketBean subtractBasketBean = new BasketBean();
		for (BasketBean basketBean : basketBeanList) {
			// 買い物かごリストより対象商品を探す
			if (basketBean.getId() == form.getId()) {
				subtractBasketBean = basketBean;
			}
		}

		if (subtractBasketBean.getOrderNum() >= 2) {
			// 注文個数が2以上だった場合、買い物かごの商品数を1減らす
			subtractBasketBean.setOrderNum(subtractBasketBean.getOrderNum() - 1);
		} else {
			// 注文個数が1だった場合、買い物かごから商品を削除
			basketBeanList.remove(subtractBasketBean);
			if (basketBeanList.size() == 0) {
				// 買い物かご内の商品が0になった場合、買い物かご情報をnullにする
				session.setAttribute("basketBeans", null);
			}
		}

		// 買い物かご内の商品一覧を表示
		return "forward:/basket/list";

	}

	/**
	 * 買い物かごから商品をすべて削除する処理
	 *
	 * @param form 買い物かごフォーム
	 *
	 * @return "forward:/basket/list" 注文一覧1ページ目
	 */
	@RequestMapping(path = "/basket/allDelete", method = RequestMethod.POST)
	public String deleteAll(@ModelAttribute BasketForm form) {

		session.setAttribute("basketBeans", null);

		return "forward:/basket/list";

	}

	/**
	 * 買い物かご内の商品一覧を表示する処理
	 *
	 * @return "basket/shopping_basket" 買い物かご画面へ
	 * @since 15.0.2 / 2021/02/16
	 */
	@RequestMapping(path = "/basket/list", method = {RequestMethod.POST, RequestMethod.GET})
	public String basketList() {
		return "basket/shopping_basket";
	}
}
