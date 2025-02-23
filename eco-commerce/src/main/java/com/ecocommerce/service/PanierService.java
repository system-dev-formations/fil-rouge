package com.ecocommerce.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecocommerce.DAO.IPanierDao;
import com.ecocommerce.DAO.UserDao;
import com.ecocommerce.DTO.PanierDTO;
import com.ecocommerce.DTO.ProduitDTO;
import com.ecocommerce.DTO.UserDTO;
import com.ecocommerce.Entity.Panier;
import com.ecocommerce.Entity.Produit;
import com.ecocommerce.Entity.Users;
import com.ecocommerce.IService.IPanierService;

@Service
public class PanierService implements IPanierService{
	

	@Autowired
	UserDao userDao;
	
	@Autowired
	IPanierDao panierDao;
	

	@Override
	public List<PanierDTO> afficherListePanier() {
		return null;
	}
	

	@Override
	public PanierDTO getpanierBuyId(Long id) {
		Optional<Users> users = this.userDao.findById(id);
		if (users.isPresent()) {
			return this.panierToPanierDTO(users.get().getPanier());
		} else {
			return null;
		}
	}

	@Override
	public String SupprimerPanier(Long id) {
		Optional<Panier> opPan = this.panierDao.findById(id);
		if (opPan.isPresent()) {
			opPan.get().getProduits().clear();
			this.panierDao.save(opPan.get());
			return "ok";
		}else {
			return null;
		}
	}

	@Override
	public PanierDTO ajouterPanier(Long id, PanierDTO pan) {
		Optional<Users> users = this.userDao.findById(id);
		if (users.isPresent()) {
			Optional<Panier> panierToSave = this.panierDao.findById(pan.getId());
			panierToSave.get().setProduits(this.panierDTOToPanier(pan).getProduits());
			Panier panier = this.panierDao.save(panierToSave.get());
			users.get().setPanier(panier);
			this.userDao.save(users.get());
			return this.panierToPanierDTO(this.userDao.save(users.get()).getPanier());
		} else {
			return null;
		}
	}
	
	
	
	@Override
	public String SupprimerUnProduitDuPanier(Long panId, Long prdId) {
		Optional<Panier> opPan = this.panierDao.findById(panId);
		if (opPan.isPresent()) {
			for (Produit prd : opPan.get().getProduits()) {
				if (prd .getId().equals(prdId)) {
					opPan.get().getProduits().remove(prd);
				}
			}
			this.panierDao.save(opPan.get());
			return "ok";
		} else {
			return null;
		}
		
	}
	
	
	public PanierDTO panierToPanierDTO(Panier pan) {
		if (pan.getProduits() != null) {
			return PanierDTO.builder()
					.id(pan.getId())
					.produits(pan.getProduits()
							.stream()
							.map(x -> this.produitToProduitDTO(x)).collect(Collectors.toList())).build();
		} else {
			return PanierDTO.builder()
					.id(pan.getId())
					.build();
		}

	}
	
	public Panier panierDTOToPanier(PanierDTO pan) {
		return Panier.builder()
		.id(pan.getId())
		.produits(pan.getProduits()
				.stream()
				.map(x -> this.produitDtoToProduit(x)).collect(Collectors.toList())).build();
	}
	
	
	private ProduitDTO produitToProduitDTO(Produit prd){
		return ProduitDTO.builder()
				.id(prd.getId())
				.image(prd.getImage())
				.nom(prd.getNom())
				.prix(prd.getPrix())
				.description(prd.getDescription()).build();
	}
	
	private Produit produitDtoToProduit(ProduitDTO prd){
		return Produit.builder()
				.id(prd.getId())
				.image(prd.getImage())
				.nom(prd.getNom())
				.prix(prd.getPrix())
				.description(prd.getDescription()).build();
	}



}
