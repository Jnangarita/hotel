package com.hotel.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import com.hotel.dao.GuestRegisterDao;
import com.hotel.dao.ReservationDao;
import com.hotel.enumerations.Messages;
import com.hotel.enumerations.Nationality;
import com.hotel.enumerations.Routes;
import com.hotel.factory.ConnectionFactory;
import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.utils.Commons;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class GuestRegisterController {

	@FXML
	private Button btnCloseGuestScreen;

	@FXML
	private Button btnSaveGuestRecord;

	@FXML
	private ComboBox<Nationality> comboNationality;

	@FXML
	private DatePicker dpBirthdate;

	@FXML
	private TextField txtLastName;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtPhone;

	@FXML
	private TextField txtReservationNumber;

	@FXML
	private Reservation reservationController;

	private ReservationDao reservationDao;

	private GuestRegisterDao guestRegisterDao;

	Commons commons = new Commons();

	public void setReservationController(Reservation reservation) {
		this.reservationController = reservation;
		txtReservationNumber.setText(this.reservationController.getIdReservation());
	}

	public GuestRegisterController() {
		var factory = new ConnectionFactory();
		this.reservationDao = new ReservationDao(factory.createConnection());
		this.guestRegisterDao = new GuestRegisterDao(factory.createConnection());
	}

	@FXML
	void closeGuestScreen(ActionEvent event) {
		int answer = JOptionPane.showConfirmDialog(null, Messages.CANCEL_GUEST_REGISTRATION.getSms(),
				Messages.TITLE_CANCEL.getSms(), JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			commons.openScreen(event, Routes.USER_MENU.getPath());
		}
	}

	@FXML
	void saveGuestRecord(ActionEvent event) {
		boolean registrationResult = false;
		boolean result = false;
		if (txtName.getText().isEmpty() || txtLastName.getText().isEmpty() || dpBirthdate.getValue() == null
				|| comboNationality.getValue().toString().isEmpty() || txtPhone.getText().isEmpty()) {
			commons.showNotificationEmptyField();
			return;
		}
		String name = txtName.getText();
		String lastName = txtLastName.getText();
		LocalDate date = dpBirthdate.getValue();
		Date birthdate = Date.valueOf(date);
		String nationality = comboNationality.getValue().toString();
		String phone = txtPhone.getText();
		String idReservation = this.reservationController.getIdReservation();
		Guest guest = new Guest(name, lastName, birthdate, nationality, phone, idReservation);
		registrationResult = guestRegisterDao.saveGuest(guest);
		if (registrationResult) {
			result = reservationDao.saveReservation(this.reservationController);
			if (result) {
				commons.showSuccessMessage(idReservation);
				commons.openScreen(event, Routes.USER_MENU.getPath());
			} else {
				commons.showErrorMessage();
			}
		}
	}

	@FXML
	void initialize() {
		List<Nationality> listPaymentMethods = Arrays.asList(Nationality.values());
		comboNationality.getItems().addAll(listPaymentMethods);
	}
}