import React from 'react';
import { Link } from 'react-router-dom';
import { money } from '../utils/format';
import { Status } from './Common';
import CarPhoto from './CarPhoto';

const gradients = ['blue', 'purple', 'green', 'orange'];

export default function CarCard({ car, index = 0, onAdd }) {
  return <article className="car-card">
    <div className={`car-image ${gradients[index % gradients.length]}`}>
      <CarPhoto car={car}/><Status active={car.carStatus}/>
    </div>
    <div className="car-body">
      <small>{car.manufacturerName || 'FUCar'} • {car.year} • {car.fuelType}</small>
      <h4>{car.carName}</h4>
      <div className="specs">
        <span><i className="bi bi-people"/> {car.seatingCapacity} chỗ</span>
        <span><i className="bi bi-speedometer2"/> {(car.mileage || 0).toLocaleString('vi-VN')} km</span>
      </div>
      <div className="price"><strong>{money(car.carRentingPricePerDay)}</strong><span>/ ngày</span></div>
      <div className="d-flex gap-2">
        <Link className="btn btn-outline-primary flex-fill" to={`/cars/${car.carId}`}>Chi tiết</Link>
        {onAdd && car.carStatus && <button className="btn btn-brand flex-fill" onClick={() => onAdd(car)}>Chọn xe</button>}
      </div>
    </div>
  </article>;
}
