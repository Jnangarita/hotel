package com.hotel.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import com.hotel.dao.GuestRegisterDao;
import com.hotel.dao.ReservationDao;
import com.hotel.enumerations.PaymentMethod;
import com.hotel.enumerations.Routes;
import com.hotel.exception.KnownExceptions;
import com.hotel.factory.ConnectionFactory;
import com.hotel.model.Reservation;
import com.hotel.utils.Util;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ReservationController {

	@FXML
	private Button btnNext;

	@FXML
	private ComboBox<PaymentMethod> comboPaymentMethod;

	@FXML
	private DatePicker dpDateCheckIn;

	@FXML
	private DatePicker dpDateCheckOut;

	@FXML
	private TextField txtReservationPrice;

	ReservationDao reservationDao;

	private GuestRegisterDao guestRegisterDao;

	Util util = new Util();

    public ReservationController() throws IOException, SQLException {
		var factory = new ConnectionFactory();
		this.reservationDao = new ReservationDao(factory.createConnection());
		this.guestRegisterDao = new GuestRegisterDao(factory.createConnection());
    }

	@FXML
	void saveReservation(ActionEvent event) {
		if (comboPaymentMethod.getValue() == null || dpDateCheckIn.getValue() == null
				|| dpDateCheckOut.getValue() == null || txtReservationPrice.getText().isEmpty()) {
			throw new KnownExceptions("Los campos no pueden ser vacíos");
		}

		String idReservation = util.generateGuestId(guestRegisterDao.getNumberOfGuestRows());
		LocalDate dateCheckIn = dpDateCheckIn.getValue();
		Date dpDateCheckInFormat = Date.valueOf(dateCheckIn);
		LocalDate dateCheckOut = dpDateCheckOut.getValue();
		Date dpDateCheckOutFormat = Date.valueOf(dateCheckOut);
		Long reservedDays = util.getReservedDays(dateCheckIn, dateCheckOut);
		Double reservePrice = Double.parseDouble(txtReservationPrice.getText()) * reservedDays;
		Reservation reservation = new Reservation(idReservation, dpDateCheckInFormat, dpDateCheckOutFormat,
				reservePrice, comboPaymentMethod.getValue().toString());
		Stage stage = (Stage) btnNext.getScene().getWindow(); // Obtiene el Stage actual
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(Routes.GUEST_REGISTER.getPath()));
			Parent root = loader.load();
			GuestRegisterController controller = loader.getController();
			controller.setReservationController(reservation);
			Scene scene = new Scene(root);
			stage.setScene(scene);
		} catch (IOException e) {
			throw new KnownExceptions("Ocurrió un error en la reservación");
		}
	}

	@FXML
	void initialize() {
		List<PaymentMethod> listPaymentMethods = Arrays.asList(PaymentMethod.values());
		comboPaymentMethod.getItems().addAll(listPaymentMethods);
	}
}