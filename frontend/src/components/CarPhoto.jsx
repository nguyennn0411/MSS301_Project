import { useState } from 'react';
import './CarPhoto.css';

export default function CarPhoto({ car, className = '' }) {
  const [failed, setFailed] = useState(false);
  const showPhoto = Boolean(car?.imageUrl) && !failed;

  return <div className={`car-photo ${className}`}>
    {showPhoto
      ? <img src={car.imageUrl} alt={car.carName} loading="lazy" onError={() => setFailed(true)}/>
      : <i className="bi bi-car-front-fill" aria-label="Chưa có ảnh xe"/>}
  </div>;
}
