package com.hotel.dao;

import java.sql.*;

import com.hotel.exception.UnknownExceptions;
import com.hotel.model.Reservation;

public class ReservationDao {
	private final Connection con;

	public ReservationDao(Connection con) {
		this.con = con;
	}

	public void saveReservation(Reservation reservation) {
		try {
			String querySave = "INSERT INTO reservas (id_reserva, fecha_entrada, fecha_salida, valor, forma_pago)"
					+ " VALUES (?, ?, ?, ?, ?)";
			final PreparedStatement statement = con.prepareStatement(querySave, Statement.RETURN_GENERATED_KEYS);
			try (statement) {
				executeQueryToSave(reservation, statement);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnknownExceptions("Ocurrió un error al tratar de registrar la reserva");
		}
	}

	private void executeQueryToSave(Reservation reservation, PreparedStatement statement) {
		try {
			statement.setString(1, reservation.getIdReservation());
			statement.setDate(2, reservation.getDateCheckIn());
			statement.setDate(3, reservation.getDateCheckOut());
			statement.setDouble(4, reservation.getPrice());
			statement.setString(5, reservation.getPaymentMethod());
			statement.execute();
			final ResultSet resultSet = statement.getGeneratedKeys();
			try (resultSet) {
				while (resultSet.next()) {
					reservation.setId(resultSet.getInt(1));
					System.out.println("Inserto la reserva: " + reservation.toString());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnknownExceptions("Ocurrió un error al tratar de registrar la reserva");
		}
	}
}