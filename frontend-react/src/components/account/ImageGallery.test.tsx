import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import ImageGallery from './ImageGallery';

describe('ImageGallery', () => {
  const mockImages = ['image1.jpg', 'image2.jpg', 'image3.jpg'];

  it('shows no images message when images array is empty', () => {
    render(<ImageGallery images={[]} />);
    expect(screen.getByText('No images available')).toBeInTheDocument();
  });

  it('shows no images message when images is null', () => {
    render(<ImageGallery images={null as any} />);
    expect(screen.getByText('No images available')).toBeInTheDocument();
  });

  it('displays main image when images are provided', () => {
    render(<ImageGallery images={mockImages} />);
    const mainImage = screen.getByAltText('Account image 1');
    expect(mainImage).toBeInTheDocument();
    expect(mainImage).toHaveAttribute('src', 'image1.jpg');
  });

  it('displays thumbnail strip when multiple images', () => {
    render(<ImageGallery images={mockImages} />);
    const thumbnails = screen.getAllByAltText(/Thumbnail/);
    expect(thumbnails).toHaveLength(3);
  });

  it('does not show thumbnails when only one image', () => {
    render(<ImageGallery images={['image1.jpg']} />);
    const thumbnails = screen.queryByAltText(/Thumbnail/);
    expect(thumbnails).not.toBeInTheDocument();
  });

  it('updates main image when thumbnail is clicked', () => {
    render(<ImageGallery images={mockImages} />);

    const mainImage = screen.getByAltText('Account image 1');
    expect(mainImage).toHaveAttribute('src', 'image1.jpg');

    const thumbnail2 = screen.getByAltText('Thumbnail 2');
    fireEvent.click(thumbnail2);

    expect(mainImage).toHaveAttribute('src', 'image2.jpg');
  });

  it('highlights selected thumbnail', () => {
    render(<ImageGallery images={mockImages} />);

    const thumbnail1 = screen.getByAltText('Thumbnail 1').parentElement;
    const thumbnail2 = screen.getByAltText('Thumbnail 2').parentElement;

    expect(thumbnail1).toHaveClass('border-blue-500');
    expect(thumbnail2).not.toHaveClass('border-blue-500');

    fireEvent.click(screen.getByAltText('Thumbnail 2'));

    expect(thumbnail1).not.toHaveClass('border-blue-500');
    expect(thumbnail2).toHaveClass('border-blue-500');
  });
});
