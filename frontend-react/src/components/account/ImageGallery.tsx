import React, { useState } from 'react';

interface ImageGalleryProps {
  images: string[];
}

const ImageGallery: React.FC<ImageGalleryProps> = ({ images }) => {
  const [mainImage, setMainImage] = useState(0);

  if (!images || images.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-md p-6 mb-6">
        <div className="aspect-w-16 aspect-h-9 bg-gray-200 rounded-lg flex items-center justify-center h-96">
          <span className="text-gray-400">No images available</span>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-md p-6 mb-6">
      {/* Main Image */}
      <div className="mb-4">
        <img
          src={images[mainImage]}
          alt={`Account image ${mainImage + 1}`}
          className="w-full h-96 object-cover rounded-lg"
        />
      </div>

      {/* Thumbnails */}
      {images.length > 1 && (
        <div className="grid grid-cols-4 md:grid-cols-6 gap-2">
          {images.map((image, index) => (
            <button
              key={index}
              onClick={() => setMainImage(index)}
              className={`rounded-lg overflow-hidden border-2 transition-all ${
                index === mainImage
                  ? 'border-blue-500 ring-2 ring-blue-300'
                  : 'border-gray-200 hover:border-gray-300'
              }`}
            >
              <img
                src={image}
                alt={`Thumbnail ${index + 1}`}
                className="w-full h-20 object-cover"
              />
            </button>
          ))}
        </div>
      )}
    </div>
  );
};

export default ImageGallery;
