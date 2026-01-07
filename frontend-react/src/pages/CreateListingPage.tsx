import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { useDropzone } from 'react-dropzone';
import toast from 'react-hot-toast';
import { useGames, useCreateAccount } from '../hooks/use-graphql';
import { AccountStatus } from '../types/graphql';
import type { CreateAccountInput } from '../types/graphql';

/**
 * Validation schema for account creation form
 */
const schema = yup.object({
  gameId: yup.string().required('Game is required'),
  title: yup.string().required('Title is required').min(5, 'Title must be at least 5 characters').max(200, 'Title must be less than 200 characters'),
  description: yup.string().optional(),
  level: yup.number().optional().positive('Level must be positive').integer('Level must be a whole number'),
  rank: yup.string().optional(),
  price: yup.number().required('Price is required').positive('Price must be positive'),
  images: yup.array().min(1, 'At least one image is required').max(5, 'Maximum 5 images allowed').required('Images are required'),
});

/**
 * CreateListingPage - Form for creating new account listings
 * Protected route - requires SELLER or ADMIN role
 */
const CreateListingPage = () => {
  const navigate = useNavigate();
  const { games, loading: gamesLoading } = useGames();
  const { createAccount, loading: creating } = useCreateAccount();

  const [imageFiles, setImageFiles] = useState<File[]>([]);
  const [imagePreviews, setImagePreviews] = useState<string[]>([]);

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<CreateAccountInput & { gameId: string }>({
    resolver: yupResolver(schema),
    defaultValues: {
      gameId: '',
      title: '',
      description: '',
      level: undefined,
      rank: '',
      price: 0,
      images: [],
    },
  });

  /**
   * Handle image uploads with drag-and-drop
   */
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    accept: {
      'image/*': ['.jpg', '.jpeg', '.png'],
    },
    maxFiles: 5,
    maxSize: 10 * 1024 * 1024, // 10MB
    onDrop: (acceptedFiles, rejectedFiles) => {
      // Handle rejected files
      if (rejectedFiles.length > 0) {
        rejectedFiles.forEach(({ file, errors }) => {
          errors.forEach((error) => {
            if (error.code === 'file-too-large') {
              toast.error(`"${file.name}" is too large. Maximum size is 10MB`);
            } else if (error.code === 'file-invalid-type') {
              toast.error(`"${file.name}" is not a valid image. Only JPG and PNG are allowed`);
            } else {
              toast.error(`"${file.name}" could not be uploaded`);
            }
          });
        });
      }

      // Process accepted files
      const newFiles = [...imageFiles];
      const newPreviews = [...imagePreviews];

      acceptedFiles.forEach((file) => {
        if (newFiles.length < 5) {
          newFiles.push(file);
          // Create preview URL
          const preview = URL.createObjectURL(file);
          newPreviews.push(preview);
        }
      });

      setImageFiles(newFiles);
      setImagePreviews(newPreviews);
      setValue('images', newPreviews);
    },
  });

  /**
   * Remove an image from the list
   */
  const removeImage = (index: number) => {
    const newFiles = imageFiles.filter((_, i) => i !== index);
    const newPreviews = imagePreviews.filter((_, i) => i !== index);

    setImageFiles(newFiles);
    setImagePreviews(newPreviews);
    setValue('images', newPreviews);
  };

  /**
   * Handle form submission
   */
  const onSubmit = async (data: CreateAccountInput & { gameId: string }) => {
    try {
      // For now, use placeholder URLs for images
      // In production, this would upload to cloud storage
      const input: CreateAccountInput = {
        ...data,
        images: data.images.length > 0 ? data.images : ['https://placeholder.example.com/default.jpg'],
      };

      const result = await createAccount({ variables: { input } });

      if (result) {
        toast.success('Account listing created successfully!');
        setTimeout(() => {
          navigate('/seller/my-listings');
        }, 1000);
      }
    } catch (error) {
      console.error('Failed to create account:', error);
      toast.error('Failed to create account listing');
    }
  };

  if (gamesLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Create Account Listing</h1>
        <p className="text-gray-600 mt-1">List your game account for sale</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        {/* Game Selection */}
        <div>
          <label htmlFor="gameId" className="block text-sm font-medium text-gray-700 mb-1">
            Game <span className="text-red-500">*</span>
          </label>
          <select
            id="gameId"
            {...register('gameId')}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            disabled={creating}
          >
            <option value="">Select a game</option>
            {games.map((game) => (
              <option key={game.id} value={game.id}>
                {game.name}
              </option>
            ))}
          </select>
          {errors.gameId && <p className="mt-1 text-sm text-red-600">{errors.gameId.message}</p>}
        </div>

        {/* Title */}
        <div>
          <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-1">
            Title <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            id="title"
            {...register('title')}
            placeholder="e.g., Level 50 Diamond Account with 100 Skins"
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            disabled={creating}
          />
          {errors.title && <p className="mt-1 text-sm text-red-600">{errors.title.message}</p>}
        </div>

        {/* Description */}
        <div>
          <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-1">
            Description
          </label>
          <textarea
            id="description"
            {...register('description')}
            rows={4}
            placeholder="Describe the account features, champions, skins, rank, etc."
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            disabled={creating}
          />
          {errors.description && <p className="mt-1 text-sm text-red-600">{errors.description.message}</p>}
        </div>

        {/* Level and Rank */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label htmlFor="level" className="block text-sm font-medium text-gray-700 mb-1">
              Level
            </label>
            <input
              type="number"
              id="level"
              {...register('level', { valueAsNumber: true })}
              placeholder="e.g., 50"
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              disabled={creating}
            />
            {errors.level && <p className="mt-1 text-sm text-red-600">{errors.level.message}</p>}
          </div>

          <div>
            <label htmlFor="rank" className="block text-sm font-medium text-gray-700 mb-1">
              Rank
            </label>
            <input
              type="text"
              id="rank"
              {...register('rank')}
              placeholder="e.g., Diamond, Master, Challenger"
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              disabled={creating}
            />
            {errors.rank && <p className="mt-1 text-sm text-red-600">{errors.rank.message}</p>}
          </div>
        </div>

        {/* Price */}
        <div>
          <label htmlFor="price" className="block text-sm font-medium text-gray-700 mb-1">
            Price ($) <span className="text-red-500">*</span>
          </label>
          <div className="relative">
            <span className="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-500">$</span>
            <input
              type="number"
              id="price"
              {...register('price', { valueAsNumber: true })}
              step="0.01"
              placeholder="0.00"
              className="w-full pl-8 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              disabled={creating}
            />
          </div>
          {errors.price && <p className="mt-1 text-sm text-red-600">{errors.price.message}</p>}
        </div>

        {/* Image Upload */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Images <span className="text-red-500">*</span>
          </label>
          <p className="text-sm text-gray-500 mb-2">Upload up to 5 images. Max 10MB each. JPG or PNG only.</p>

          {/* Dropzone */}
          <div
            {...getRootProps()}
            className={`border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition-colors ${
              isDragActive
                ? 'border-blue-500 bg-blue-50'
                : 'border-gray-300 hover:border-gray-400'
            }`}
          >
            <input {...getInputProps()} />
            <svg className="mx-auto h-12 w-12 text-gray-400" stroke="currentColor" fill="none" viewBox="0 0 48 48">
              <path
                d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02"
                strokeWidth={2}
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
            <p className="mt-2 text-sm text-gray-600">
              {isDragActive ? 'Drop images here' : 'Drag & drop images here, or click to select'}
            </p>
          </div>

          {/* Image Previews */}
          {imagePreviews.length > 0 && (
            <div className="mt-4 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
              {imagePreviews.map((preview, index) => (
                <div key={index} className="relative group">
                  <img
                    src={preview}
                    alt={`Preview ${index + 1}`}
                    className="w-full h-32 object-cover rounded-lg border border-gray-200"
                  />
                  <button
                    type="button"
                    onClick={() => removeImage(index)}
                    className="absolute top-2 right-2 bg-red-500 text-white rounded-full p-1 opacity-0 group-hover:opacity-100 transition-opacity"
                    disabled={creating}
                  >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              ))}
            </div>
          )}
          {errors.images && <p className="mt-1 text-sm text-red-600">{errors.images.message}</p>}
        </div>

        {/* Submit Buttons */}
        <div className="flex justify-end space-x-4 pt-4 border-t">
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
            disabled={creating}
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={creating || isSubmitting}
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
          >
            {creating ? 'Creating...' : 'Create Listing'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateListingPage;
