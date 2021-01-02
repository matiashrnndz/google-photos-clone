using System;
using Microsoft.EntityFrameworkCore.Migrations;

namespace DataAccess.Migrations
{
    public partial class AddPhotoDetails : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "Latitude",
                table: "Photo",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "Location",
                table: "Photo",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "Longitude",
                table: "Photo",
                nullable: true);

            migrationBuilder.AddColumn<DateTime>(
                name: "TakenAt",
                table: "Photo",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Latitude",
                table: "Photo");

            migrationBuilder.DropColumn(
                name: "Location",
                table: "Photo");

            migrationBuilder.DropColumn(
                name: "Longitude",
                table: "Photo");

            migrationBuilder.DropColumn(
                name: "TakenAt",
                table: "Photo");
        }
    }
}
