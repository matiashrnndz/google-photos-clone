using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Domain
{
    public class Album
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public List<AlbumPhoto> AlbumPhotos { get; set; }

        public Album()
        {
            AlbumPhotos = new List<AlbumPhoto>();
        }

        public void AddPhoto(Photo photo)
        {
            AlbumPhoto ph = new AlbumPhoto()
            {
                AlbumId = this.Id,
                Album = this,
                PhotoId = photo.Id,
                Photo = photo
            };
            AlbumPhotos.Add(ph);
        }

        public List<Photo> Photos()
        {
            return this.AlbumPhotos.Select(ap => ap.Photo).ToList();
        }
    }
}
